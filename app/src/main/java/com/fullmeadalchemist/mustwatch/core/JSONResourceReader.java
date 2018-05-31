/*
 * Copyright (c) 2017 Full Mead Alchemist, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullmeadalchemist.mustwatch.core;


import android.content.res.Resources;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import timber.log.Timber;

import static com.fullmeadalchemist.mustwatch.core.UnitMapper.toMass;
import static com.fullmeadalchemist.mustwatch.core.UnitMapper.toVolume;


/**
 * An object for reading from a JSON resource file and constructing an object from that resource file using Gson.
 * https://stackoverflow.com/a/24023898/940217
 */
public class JSONResourceReader {

    private static final String TAG = JSONResourceReader.class.getSimpleName();
    private String jsonString;

    /**
     * Read from a resources file and create a {@link JSONResourceReader} object that will allow the creation of other
     * objects from this resource.
     *
     * @param resources An application {@link Resources} object.
     * @param id        The id for the resource to load, typically held in the raw/ folder.
     */
    public JSONResourceReader(Resources resources, int id) {
        InputStream resourceReader = resources.openRawResource(id);
        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            Timber.e(e, "Unhandled exception while using JSONResourceReader\n");
        } finally {
            try {
                resourceReader.close();
            } catch (Exception e) {
                Timber.e(e, "Unhandled exception while using JSONResourceReader");
            }
        }

        jsonString = writer.toString();
    }

    /**
     * Build an object from the specified JSON resource using Gson.
     *
     * @param type The type of the object to build.
     * @return An object of type T, with member fields populated using Gson.
     */
    public <T> T constructUsingGson(Class<T> type) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Type volumeType = new TypeToken<Quantity<Volume>>() {
        }.getType();
        JsonDeserializer<Quantity<Volume>> volumeDeserializer = new VolumeDeserializer();
        gsonBuilder.registerTypeAdapter(volumeType, volumeDeserializer);

        Type massType = new TypeToken<Quantity<Mass>>() {
        }.getType();
        JsonDeserializer<Quantity<Mass>> massDeserializer = new MassDeserializer();
        gsonBuilder.registerTypeAdapter(massType, massDeserializer);

        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonString, type);
    }


    private class VolumeDeserializer implements JsonDeserializer<Quantity<Volume>> {
        public Quantity<Volume> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String volString = json.getAsJsonPrimitive().getAsString();
            if (!TextUtils.isEmpty(volString)) {
                String[] components = volString.split(" ");
                return toVolume(components[0], components[1]);
            }
            Timber.e("Failed to parse volume from string\"%s\"", volString);
            return null;
        }
    }

    private class MassDeserializer implements JsonDeserializer<Quantity<Mass>> {
        public Quantity<Mass> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String volString = json.getAsJsonPrimitive().getAsString();
            if (!TextUtils.isEmpty(volString)) {
                String[] components = volString.split(" ");
                return toMass(components[0], components[1]);
            }
            Timber.e("Failed to parse mass from string\"%s\"", volString);
            return null;
        }
    }
}