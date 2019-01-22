package nycuro.gui.list;

import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

/**
 * author: Empire92
 * FAWE Project
 * API 1.0.0
 */
public class DelegateFormWindow extends FormWindow {

    private final FormWindow parent;

    public DelegateFormWindow(FormWindow parent) {
        this.parent = parent;
    }

    @Override
    public String getJSONData() {
        return parent.getJSONData();
    }

    @Override
    public FormResponse getResponse() {
        return parent.getResponse();
    }

    @Override
    public void setResponse(String s) {
        parent.setResponse(s);
    }
}