/*
 * NMRFx Processor : A Program for Processing NMR Data 
 * Copyright (C) 2004-2018 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nmrfx.processor.gui.spectra;

import javafx.scene.control.ContextMenu;
import javafx.stage.Window;
import org.nmrfx.processor.gui.PolyChart;

/**
 *
 * @author brucejohnson
 */
public abstract class ChartMenu {

    PolyChart chart;
    ContextMenu chartMenu;

    public ChartMenu(PolyChart chart) {
        this.chart = chart;
        makeChartMenu();
    }

    abstract void makeChartMenu();

    public void show(Window window, double x, double y) {
        chartMenu.show(window, x, y);
    }

}