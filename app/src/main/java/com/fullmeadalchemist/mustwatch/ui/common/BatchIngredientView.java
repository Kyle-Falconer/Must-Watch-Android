/*
 * Copyright (c) 2018 Full Mead Alchemist, LLC.
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

package com.fullmeadalchemist.mustwatch.ui.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient;

public class BatchIngredientView extends LinearLayout {
    private BatchIngredient batchIngredient;

    private TextView ingredientLabel;
    private TextView qtyAmount;

    public BatchIngredientView(Context context) {
        super(context);
        init();
    }

    public BatchIngredientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatchIngredientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.batch_ingredient_view, this);
        ingredientLabel = findViewById(R.id.ingredient_label);
        qtyAmount = findViewById(R.id.quantity_label);
        updateUI();
    }

    private void updateUI() {
        // FIXME: This could crash if the ingredient doesn't have a matching resource ID.
        // FIXME: Looking up the string resource in this way could lead to performance issues.
        if (this.batchIngredient == null) {
            return;
        }
        int ingredientResID = getResources().getIdentifier(this.batchIngredient.getIngredientId(),
                "string", getContext().getPackageName());
        ingredientLabel.setText(getContext().getString(ingredientResID));
        if (this.batchIngredient.getQuantityVol() != null) {
            qtyAmount.setText(this.batchIngredient.getQuantityVol().toString());
        } else if (this.batchIngredient.getQuantityMass() != null) {
            qtyAmount.setText(this.batchIngredient.getQuantityMass().toString());
        } else {
            qtyAmount.setText(getContext().getText(R.string.no_ingredient));
        }
    }

    public void setBatchIngredient(BatchIngredient batchIngredient) {
        this.batchIngredient = batchIngredient;
        updateUI();
    }

}
