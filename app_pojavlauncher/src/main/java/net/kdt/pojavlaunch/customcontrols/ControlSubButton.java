package net.kdt.pojavlaunch.customcontrols;

public class ControlSubButton extends ControlButton {

    public ControlDrawer parentDrawer;

    public ControlSubButton(ControlLayout layout, ControlData properties, ControlDrawer parentDrawer) {
        super(layout, properties);
        this.parentDrawer = parentDrawer;
        filterProperties();
    }

    private void filterProperties(){
        mProperties.height = parentDrawer.getProperties().height;
        mProperties.width = parentDrawer.getProperties().width;
        mProperties.isDynamicBtn = false;

        setProperties(mProperties, false);
    }




    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if(parentDrawer != null){
            params.width = (int)parentDrawer.mProperties.width;
            params.height = (int)parentDrawer.mProperties.height;
        }

        super.setLayoutParams(params);

    }
}
