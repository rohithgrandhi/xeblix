package test.org.xeblix.configuration;

import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.xeblix.configuration.RemoteConfigurationContainer;

public class TestParseJSONConfiguration {

	@Test
	public void testInvalidButtonConfiguration() throws Exception{
		
		//start with null parameters
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(null, null);
			fail("Should have thrown an IllegalArgumentException");
		}catch(IllegalArgumentException ex){
			assertEquals("This method does not accept null parameters.", ex.getMessage());
		}
		
		//############################################
		//now try a valid jsonObject but one that contains no button array
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject("{}"), "testRemote");
			fail("Should have thrown an RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testRemote has an invalid configuration. The RemoteConfiguration does not " +
				"contain a required JSONArray of buttons.", ex.getMessage());
		}
		
		//############################################
		//now try a valid jsonObject with a button's array of ints
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[1,2,3]}"), "testRemote");
			fail("Should have thrown an RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testRemote has an invalid configuration. The buttons jsonArray " +
				"must contain only JSONObjects.", ex.getMessage());
		}
		
		//############################################
		//next try a valid JSONObject with an array of jsonObjects without the proper button attributes
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[{},{}]}"), "testremote");
			fail("Should have thrown a RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testremote has an invalid configuration. A button JSONObject must " +
				"contain the following properties: screen, target, command, and label. One or more of these " +
				"properties is missing from the button configuration: {}", ex.getMessage());
		}
		
		//############################################
		//now try a valid JSONObject with a valid array of buttons, but invalid screens value
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[{screen:INVALID, target:ROOT_POWER,command:POWER, label:Power}]}"), "testremote");
			fail("Should have thrown a RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testremote has an invalid configuration. " +
				"The button configuration: {\"screen\":\"INVALID\",\"command\":\"POWER\"," +
				"\"target\":\"ROOT_POWER\",\"label\":\"Power\"} has an unknown Screen value.", 
				ex.getMessage());
		}
		
		//############################################
		//now try a valid JSONObject with a valid array of buttons, but invalid target value
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[{screen:ROOT, target:INVALID,command:POWER, label:Power}]}"), "testremote");
			fail("Should have thrown a RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testremote has an invalid configuration. " +
				"The button configuration: {\"screen\":\"ROOT\",\"command\":\"POWER\"," +
				"\"target\":\"INVALID\",\"label\":\"Power\"} has an unknown Target value.", 
				ex.getMessage());
		}
		
		//##############################################
		//try invalid combination of screen and target
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[{screen:ROOT, target:GESTURE_SCREEN_1,command:POWER, label:Power}]}"), "testremote");
			fail("Should have thrown a RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The UserInputTarget: Gesture Screen 1 is not valid for the screen: Root.", 
				ex.getMessage());
		}
		
		//#####################################
		//try some invalid commands
		//try command with array of array's first
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[{screen:ROOT, target:ROOT_OK,command:[[{blah:blah}],[{blah:blah}]], label:Power}]}"), "testremote");
			fail("Should have thrown a RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testremote has an invalid configuration. The button configuration: " +
					"{\"screen\":\"ROOT\",\"command\":[[{\"blah\":\"blah\"}],[{\"blah\":\"blah\"}]]," +
					"\"target\":\"ROOT_OK\",\"label\":\"Power\"} must be either a String/int or an array " +
					"of String/ints.",ex.getMessage());
		}
		
		//now try array of JSONObjects
		try{
			RemoteConfigurationContainer.parseButtonConfiguration(new JSONObject(
				"{buttons:[{screen:ROOT, target:ROOT_OK,command:[{blah:blah},{blah:blah}], label:Power}]}"), "testremote");
			fail("Should have thrown a RuntimeException");
		}catch(RuntimeException ex){
			assertEquals("The remote: testremote has an invalid configuration. The button configuration: " +
					"{\"screen\":\"ROOT\",\"command\":[{\"blah\":\"blah\"},{\"blah\":\"blah\"}]," +
					"\"target\":\"ROOT_OK\",\"label\":\"Power\"} must be either a String/int or an array " +
					"of String/ints.",ex.getMessage());
		}
		
	}
	
	@Test
	public void testValid() throws Exception{
		JSONObject jsonObject = new JSONObject("{remoteType:LIRC,name:vip1200,label:DVR, repeatCount: 2, buttons:" +
			"[" +
				"{screen:ROOT, target:ROOT_POWER,command:POWER, label:Power}," +
				"{screen:ROOT, target:ROOT_OK,command:OK, label:OK}," +
				"{screen:ROOT, target:ROOT_MENU,command:MENU, label:Menu}," +
				"{screen:ROOT, target:ROOT_MENU,command:GUIDE, label:Guide}," +
				"{screen:ROOT, target:ROOT_MENU,command:RECORDEDTV, label:Recorded TV}," +
				"{screen:ROOT, target:ROOT_MENU,command:gointeractive, label:Go Interactive}," +
				"{screen:ROOT, target:ROOT_MENU,command:VIDEOONDEMAND, label:Video On Demand}," +
				
				"{screen:DIRECTION, target:GESTURE_SCREEN_1,command:BACK, label:Back}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_3,command:OK, label:OK}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_4,command:RECORD, label:Record}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_6,command:Up, label:Up}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_7,command:CHPG+, label:Page Up}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_8,command:DOWN, label:Down}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_9,command:CHPG-, label:Page Down}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_10,command:LEFT, label:Left}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_11,command:REW, label:Back 24 Hours}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_12,command:RIGHT, label:Right}," +
				"{screen:DIRECTION, target:GESTURE_SCREEN_13,command:FF, label:Forward 24 Hours}," +
				
				"{screen:SPEED, target:GESTURE_SCREEN_3,command:PLAY, label:Play}," +
				"{screen:SPEED, target:GESTURE_SCREEN_4,command:REPLAY, label:Replay}," +
				"{screen:SPEED, target:GESTURE_SCREEN_5,command:FWD, label:Forward}," +
				"{screen:SPEED, target:GESTURE_SCREEN_6,command:PAUSE, label:Pause}," +
				"{screen:SPEED, target:GESTURE_SCREEN_7,command:PAUSE, label:Pause}," +
				"{screen:SPEED, target:GESTURE_SCREEN_8,command:STOP, label:Stop}," +
				"{screen:SPEED, target:GESTURE_SCREEN_9,command:STOP, label:Stop}," +
				"{screen:SPEED, target:GESTURE_SCREEN_10,command:REW, label:Rewind}," +
				"{screen:SPEED, target:GESTURE_SCREEN_11,command:REW, label:Rewind}," +
				"{screen:SPEED, target:GESTURE_SCREEN_12,command:FF, label:Fast Forward}," +
				"{screen:SPEED, target:GESTURE_SCREEN_13,command:FF, label:Fast Forward}," +
				
				"{screen:CHANNEL, target:GESTURE_SCREEN_3,command:OK, label:OK}," +
				"{screen:CHANNEL, target:GESTURE_SCREEN_6,command:CHPG+, label:Channel Up}," +
				"{screen:CHANNEL, target:GESTURE_SCREEN_7,command:CHPG+, label:Channel Up}," +
				"{screen:CHANNEL, target:GESTURE_SCREEN_8,command:CHPG-, label:Channel Down}," +
				"{screen:CHANNEL, target:GESTURE_SCREEN_9,command:CHPG-, label:Channel Down}," +
				
				"{screen:VOLUME, target:GESTURE_SCREEN_3,command:[1,2,3], label:Channel Down}" +
			"]}");
		
		RemoteConfigurationContainer.parseButtonConfiguration(jsonObject, "test Remote");
	}
	
}
