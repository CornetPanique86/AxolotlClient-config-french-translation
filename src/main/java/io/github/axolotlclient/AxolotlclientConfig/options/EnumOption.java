package io.github.axolotlclient.AxolotlclientConfig.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.AxolotlclientConfig.screen.widgets.EnumOptionWidget;
import io.github.axolotlclient.AxolotlclientConfig.commands.CommandResponse;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.*;

public class EnumOption extends OptionBase<String> {

    private int i;

    protected String[] values;

    public EnumOption(String name, Object[] e, String def) {
        super(name, def);
        List<String> l = new ArrayList<>();
        for(Object v:e){
            l.add(v.toString());
        }
        values = l.toArray(new String[0]);

        setDefaults();
    }

    public EnumOption(String name, String[] e, String def) {
        super(name, def);
        values = e;
        setDefaults();
    }

    public EnumOption(String name, String tooltipKeyPrefix, String[] e, String def) {
        super(name, tooltipKeyPrefix, def);
        values = e;
        setDefaults();
    }

    public EnumOption(String name, String tooltipKeyPrefix, ChangedListener<String> onChange, String[] e, String def) {
        super(name, tooltipKeyPrefix, onChange, def);
        values = e;
        setDefaults();
    }

    public EnumOption(String name, ChangedListener<String> onChange, String[] e, String def) {
        super(name, onChange, def);
        values = e;
        setDefaults();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        for(int i=0; i<values.length;i++){
            String v = values[i];
            if(Objects.equals(v, element.getAsString())){
                this.i = i;
                break;
            }
        }
    }

    @Override
    public void set(String value) {
        for(int i=0;i< values.length; i++){
            String v = values[i];
            if(Objects.equals(v, value)){
                this.i=i;
                changeCallback.onChanged(get());
                break;
            }
        }
    }

    @Override
    public void setDefaults() {
        if(def==null){
            i=0;
            return;
        }

        for(int i=0;i< values.length; i++){
            String v = values[i];
            if(Objects.equals(v, def)){
                this.i=i;
                break;
            }
        }
    }

    public String get(){
        return values[i];
    }

    public String next() {
        i++;
        if(i > values.length-1)i=0;
        changeCallback.onChanged(get());
        return get();
    }

    public String last(){
        i--;
        if(i<0)i=values.length-1;
        changeCallback.onChanged(get());
        return get();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(values[i]);
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){
            if(args[0].equals("next")){
                next();
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            } else if(args[0].equals("last")){
                last();
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }

            for (int i=0;i<values.length;i++){
                if(args[0].equalsIgnoreCase(values[i])){
                    this.i=i;
                    changeCallback.onChanged(get());
                    return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+" (Index: "+i+")!");
                }
            }

            try {
                int value = Integer.parseInt(args[0]);
                if(value>values.length-1 || value < 0){
                    throw new IndexOutOfBoundsException();
                }
                i=value;
                changeCallback.onChanged(get());
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+" (Index: "+i+")!");
            } catch (IndexOutOfBoundsException e){
                return new CommandResponse(false, "Please specify an index within the bounds of 0<=i<"+values.length+"!");
            } catch (NumberFormatException ignored){
                return new CommandResponse(false, "Please specify either next, last or an index for a specific value!");
            }
        }
        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public List<String> getCommandSuggestions(String[] args) {
        if(args.length == 0) {
            return Arrays.asList(values);
        }
        return Collections.emptyList();
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height) {
        return new EnumOptionWidget(0, x, y, this);
    }
}
