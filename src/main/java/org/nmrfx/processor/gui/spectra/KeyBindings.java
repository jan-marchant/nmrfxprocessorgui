/*
 * NMRFx Processor : A Program for Processing NMR Data 
 * Copyright (C) 2004-2017 One Moon Scientific, Inc., Westfield, N.J., USA
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.nmrfx.processor.gui.KeyMonitor;
import org.nmrfx.processor.gui.PeakPicking;
import org.nmrfx.processor.gui.PolyChart;
import org.nmrfx.processor.gui.SpectrumStatusBar;

/**
 *
 * @author Bruce Johnson
 */
public class KeyBindings {

    KeyMonitor keyMonitor = new KeyMonitor();
    PolyChart chart;
    Map<String, Consumer> keyActionMap = new HashMap<>();
    static Map<String, BiConsumer<String, PolyChart>> globalKeyActionMap = new HashMap<>();

    public KeyBindings(PolyChart chart) {
        this.chart = chart;
    }

    public static void registerGlobalKeyAction(String keyString, BiConsumer<String, PolyChart> action) {
        // add firstchar so that key processing doesn't clear keyMonitor before a two key string is typed
        String firstChar = keyString.substring(0, 1);
        if (!globalKeyActionMap.containsKey(firstChar)) {
            globalKeyActionMap.put(firstChar, null);
        }
        globalKeyActionMap.put(keyString, action);
    }

    public void keyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (null != code) {
            switch (code) {
                case DOWN:
                    if (keyEvent.isShiftDown()) {
                        List<DatasetAttributes> dataAttrs = chart.getDatasetAttributes();
                        dataAttrs.stream().forEach(d -> d.rotateDim(1, -1));
                        chart.getController().updateAttrDims();
                        chart.full();
                        chart.focus();
                    } else {
                        if (chart.is1D()) {
                            chart.incrementRow(-1);
                        } else {
                            chart.incrementPlane(2, -1);
                        }
                    }
                    keyEvent.consume();
                    break;
                case UP:
                    if (keyEvent.isShiftDown()) {
                        List<DatasetAttributes> dataAttrs = chart.getDatasetAttributes();
                        dataAttrs.stream().forEach(d -> d.rotateDim(1, 1));
                        chart.getController().updateAttrDims();
                        chart.full();
                        chart.focus();
                    } else {
                        if (chart.is1D()) {
                            chart.incrementRow(1);
                        } else {
                            chart.incrementPlane(2, 1);
                        }
                    }
                    keyEvent.consume();
                    break;
                case RIGHT:
                    chart.incrementPlane(3, 1);
                    keyEvent.consume();
                    break;
                case LEFT:
                    chart.incrementPlane(3, -1);
                    keyEvent.consume();
                    break;
                case ENTER:
                    keyMonitor.complete();
                    keyEvent.consume();
                    break;
                case DELETE:
                    keyMonitor.complete();
                    keyEvent.consume();
                    chart.deleteSelectedPeaks();
                    chart.refresh();
                    break;
                case BACK_SPACE:
                    keyMonitor.complete();
                    keyEvent.consume();
                    chart.deleteSelectedPeaks();
                    chart.refresh();
                    break;
                default:
                    break;
            }
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void keyTyped(KeyEvent keyEvent) {
        Pattern pattern = Pattern.compile("jz([0-9]+)");
        long time = System.currentTimeMillis();
        String keyChar = keyEvent.getCharacter();
        if (keyChar.equals(" ")) {
            String keyString = keyMonitor.getKeyString();
            if (keyString.equals("")) {
                keyMonitor.clear();
                chart.focus();
                chart.showHitPeak(chart.getMouseX(), chart.getMouseY());
                return;
            }
        }
        keyMonitor.storeKey(keyChar);
        String keyString = keyMonitor.getKeyString();
        String shortString = keyString.substring(0, Math.min(2, keyString.length()));
        keyString = keyString.trim();
        // note always break on a single character that is used in a two character sequence
        // otherwise the keystring will be cleared and the multiple key event will never be processed
        if (keyActionMap.containsKey(shortString)) {
            Consumer action = keyActionMap.get(shortString);
            if (action != null) {
                action.accept(chart);
                keyMonitor.clear();
            }
            return;
        } else if (globalKeyActionMap.containsKey(shortString)) {
            BiConsumer<String, PolyChart> action = globalKeyActionMap.get(shortString);
            if (action != null) {
                action.accept(shortString, chart);
                keyMonitor.clear();
            }
            return;
        }
        switch (shortString) {
            case "a":
                break;

            case "aa":
            case "as":
                List<DatasetAttributes> activeData = chart.getActiveDatasetAttributes();
                if (activeData.size() == 1) {
                    DatasetAttributes datasetAttr = activeData.get(0);
                    double pickX = chart.getAxis(0).getValueForDisplay(chart.getMouseX()).doubleValue();
                    double pickY = chart.getAxis(1).getValueForDisplay(chart.getMouseY()).doubleValue();
                    PeakPicking.pickAtPosition(chart, datasetAttr, pickX, pickY, shortString.equals("as"), false);
                    chart.setPeakStatus(true);
                    keyMonitor.clear();
                    chart.drawPeakLists(true);
                } else {
                    keyMonitor.clear();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Must have one dataset displayed");
                    alert.showAndWait();
                    return;
                }
                break;
            case "c":
                break;

            case "c1":
                chart.setHasMiddleMouseButton(false);
                keyMonitor.clear();
                break;
            case "c3":
                chart.setHasMiddleMouseButton(true);
                keyMonitor.clear();
                break;
            case "cc":
                SpectrumStatusBar statusBar = chart.getController().getStatusBar();
                if (statusBar != null) {
                    statusBar.setCursor(Cursor.CROSSHAIR);
                }
                keyMonitor.clear();
                break;
            case "cs":
                statusBar = chart.getController().getStatusBar();
                if (statusBar != null) {
                    statusBar.setCursor(SpectrumStatusBar.SEL_CURSOR);
                }
                keyMonitor.clear();
                break;
            case "d":
                break;

            case "df":
                if (chart.getController().hasSlider()) {
                    chart.getController().getSlider().freezePeaks(false);
                }
                keyMonitor.clear();
                break;
            case "dt":
                if (chart.getController().hasSlider()) {
                    chart.getController().getSlider().thawPeaks(false);
                }
                keyMonitor.clear();
                break;
            case "ds":
                if (chart.getController().hasSlider()) {
                    chart.getController().getSlider().tweakPeaks(false);
                }
                keyMonitor.clear();
                break;

            case "da":
                if (!chart.getController().hasSlider()) {
                    chart.getController().showPeakSlider();
                }
                keyMonitor.clear();
                break;

            case "dq":
                if (chart.getController().hasSlider()) {
                    chart.getController().removePeakSlider(null);
                }
                keyMonitor.clear();
                break;

            case "p":
                break;
            case "u":
                break;
            case "uu":
                chart.getController().undo();
                keyMonitor.clear();
                break;
            case "ur":
                chart.getController().redo();
                keyMonitor.clear();
                break;
            case "v":
                break;
            case "ve":
                chart.expand();
                keyMonitor.clear();
                break;
            case "vf":
                chart.full();
                keyMonitor.clear();
                break;
            case "vi":
                chart.zoom(1.2);
                keyMonitor.clear();
                break;
            case "vo":
                chart.zoom(0.8);
                keyMonitor.clear();
                break;
            case "vs":
                chart.swapView();
                keyMonitor.clear();
                break;
            case "vp":
                chart.popView();
                keyMonitor.clear();
                break;
            case "j":
                break;
            case "jx":
            case "jy":
            case "jz":
                // fixme what about a,b,c..
                int iDim = keyString.charAt(1) - 'x';
                switch (keyString.substring(2)) {
                    case "f":
                        chart.full(iDim);
                        chart.refresh();
                        keyMonitor.clear();
                        break;
                    case "m":
                        if (iDim > 1) {
                            chart.gotoMaxPlane();
                            chart.refresh();
                        }
                        keyMonitor.clear();
                        break;
                    case "c":
                        chart.center(iDim);
                        chart.refresh();
                        keyMonitor.clear();
                        break;
                    case "b":
                        if (iDim > 1) {
                            chart.firstPlane(2);
                            chart.refresh();
                        }
                        keyMonitor.clear();
                        break;
                    case "t":
                        if (iDim > 1) {
                            chart.lastPlane(2);
                            chart.refresh();
                        }
                        keyMonitor.clear();
                        break;

                    default:
                        if (keyString.length() > 2) {
                            if (keyMonitor.isComplete()) {
                                if (iDim > 1) {
                                    Matcher matcher = pattern.matcher(keyString);
                                    if (matcher.matches()) {
                                        String group = matcher.group(1);
                                        int plane = Integer.parseInt(group);
                                        chart.setAxis(2, plane, plane);
                                        chart.refresh();
                                    }
                                }
                                keyMonitor.clear();
                            }
                        }
                }
                break;
            default:
                keyMonitor.clear();
        }

    }
}
