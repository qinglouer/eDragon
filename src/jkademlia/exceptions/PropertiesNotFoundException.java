package jkademlia.exceptions;

import java.util.List;

public class PropertiesNotFoundException extends Exception{
	private static final long serialVersionUID = 6877008853461035000L;
	
	private List<String> properties;

    public PropertiesNotFoundException(List<String> properties){
        super();
        this.properties = properties;
    }

    public List<String> getProperties(){
        return this.properties;
    }

    public String getMessage(){
        String message = "Properties not found: ";
        for (String property : properties)
            message += property + ", ";
        message = message.substring(0, message.length() - 2);
        return message;
    }

}
