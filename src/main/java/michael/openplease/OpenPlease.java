package michael.openplease;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenPlease implements ModInitializer {
	public static final String MOD_ID = "open-please";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.error("Open Please has successfully started!");
	}
}