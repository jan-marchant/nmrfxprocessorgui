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
package org.nmrfx.processor.gui.annotations;

import org.nmrfx.graphicsio.GraphicsContextInterface;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brucejohnson
 */
public class AnnoLineWithText extends AnnoLine {

    String text;
    double textX;
    double textY;

    public AnnoLineWithText(String text, double textX, double textY, double x1, double y1, double x2, double y2,
                            POSTYPE xPosType, POSTYPE yPosType) {
        super(x1,y1,x2,y2,xPosType,yPosType);
        this.text=text;
        this.textX=textX;
        this.textY=textY;
    }

    @Override
    public void draw(GraphicsContextInterface gC, double[][] bounds, double[][] world) {
        super.draw(gC,bounds,world);
        double xp1 = xPosType.transform(textX, bounds[0], world[0]);
        double yp1 = yPosType.transform(textY, bounds[1], world[1]);
        gC.setStroke(stroke);
        gC.setLineWidth(lineWidth);
        gC.fillText(text, xp1, yp1);
    }

}
