package cn.com.Weather;

import java.util.List;

import cn.com.webxml.ArrayOfString;
import cn.com.webxml.WeatherWS;
import cn.com.webxml.WeatherWSSoap;

public class Weather {
	public void getWeather(String cityname) {
		// Create an instance object
		WeatherWS weatherWS = new WeatherWS();
		// Create an API using instance object
		WeatherWSSoap weatherWSSoap = weatherWS.getWeatherWSSoap();
		// Inquiry weather by passing city name to the API
		ArrayOfString weather = weatherWSSoap.getWeather(cityname, null);
		if (weather != null) {
			// Put the acquired information in to a list and traverse it 
			List<String> weathers = weather.getString();
			weathers.forEach(s -> System.out.println(s));
		}
 
	}
	
}
