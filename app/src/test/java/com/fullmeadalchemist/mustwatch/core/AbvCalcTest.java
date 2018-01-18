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

package com.fullmeadalchemist.mustwatch.core;

import org.junit.Test;

import static com.fullmeadalchemist.mustwatch.core.BrewFormulae.calcAbvPct;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class AbvCalcTest {

    @Test
    public void simple_abv_calc_isCorrect() throws Exception {
        abv_calc_tester(1.118, 1.001, 17.08);
    }

    @Test
    public void one_og_abv_calc_isCorrect() throws Exception {
        abv_calc_tester(1.0, 0.958, 4.97);
    }

    @Test
    public void non_one_og_abv_calc_isCorrect() throws Exception {
        abv_calc_tester(1.110, 1.089, 3.30);
    }

    @Test
    public void one_fg_abv_calc_isCorrect() throws Exception {
        abv_calc_tester(1.118, 1.0, 17.21);
    }

    @Test
    public void strong_abv_calc_isCorrect() throws Exception {
        abv_calc_tester(1.118, 0.958, 22.35);
    }

    private void abv_calc_tester(double sgStart, double sgFinal, double expectedAbv) {
        double abv = calcAbvPct(sgStart, sgFinal);
        assertThat(abv, is(closeTo(expectedAbv, 0.01d)));
    }
}
