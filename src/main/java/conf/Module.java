package conf;

import java.net.URI;
import java.net.URISyntaxException;

import ninja.conf.FrameworkModule;
import ninja.conf.NinjaClassicModule;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

public class Module extends FrameworkModule {

    private final NinjaProperties ninjaProperties;

    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    @Override
    protected void configure() {
    	// if we are in production 
    	if(ninjaProperties.isProd()) {
    		try {
    			URI dbUri = new URI(ninjaProperties.get("heroku.db.connection"));
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                int port = dbUri.getPort();

                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        		
        		((NinjaPropertiesImpl)ninjaProperties).setProperty("db.connection.url", dbUrl);
        		((NinjaPropertiesImpl)ninjaProperties).setProperty("db.connection.username", username);
        		((NinjaPropertiesImpl)ninjaProperties).setProperty("db.connection.password", password);
        		 			
    		} catch (URISyntaxException urise) {
    			// if we haven't got a valid heroku.db url in production.. throw unrecoverable
    			throw new RuntimeException(urise);
    		}
    	}
        install(new NinjaClassicModule(ninjaProperties));
    }
}