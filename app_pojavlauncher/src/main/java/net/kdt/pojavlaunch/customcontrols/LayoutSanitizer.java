package net.kdt.pojavlaunch.customcontrols;

import java.util.Iterator;
import java.util.List;

public class LayoutSanitizer {

    // Maybe add more conditions here later?
    private static boolean isInvalidFormula(String formula) {
        return formula.contains("Infinity");
    }

    private static boolean isSaneData(ControlData controlData) {
        if(controlData.getWidth() == 0 || controlData.getHeight() == 0) return false;
        if(isInvalidFormula(controlData.dynamicX) || isInvalidFormula(controlData.dynamicY)) return false;
        return true;
    }

    private static ControlData getControlData(Object dataEntry) {
        if(dataEntry instanceof ControlData) {
            return (ControlData) dataEntry;
        }else if(dataEntry instanceof ControlDrawerData) {
            return ((ControlDrawerData) dataEntry).properties;
        }else throw new RuntimeException("Encountered wrong type during ControlData sanitization");
    }

    private static boolean sanitizeList(List<?> controlDataList) {
        boolean madeChanges = false;
        Iterator<?> iterator = controlDataList.iterator();
        while(iterator.hasNext()) {
            ControlData controlData = getControlData(iterator.next());
            if(!isSaneData(controlData)) {
                madeChanges = true;
                iterator.remove();
            }
        }
        return madeChanges;
    }

    /**
     * Check all buttons in a control layout and ensure they're sane (contain values valid enough
     * to be displayed properly). Removes any buttons deemed not sane.
     * @param controls the original control layout.
     * @return whether the sanitization process made any changes to the layout
     */
    public static boolean sanitizeLayout(CustomControls controls) {
        boolean madeChanges = sanitizeList(controls.mControlDataList);
        if(sanitizeList(controls.mDrawerDataList)) madeChanges = true;
        if(sanitizeList(controls.mJoystickDataList)) madeChanges = true;
        return madeChanges;
    }
}
