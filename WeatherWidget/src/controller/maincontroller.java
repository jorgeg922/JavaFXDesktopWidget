package controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class maincontroller implements Initializable{
	public Circle status_background, status_button;
	public TextField citySearch;
	public Text cityName, cityTemp;
	public boolean textFieldVisibility = false;
	public FillTransition fill;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//hide the input field
		citySearch.setVisible(false);
		
		//show/hide input field
		status_button.setOnMouseClicked(e ->{
			if(!textFieldVisibility) {
				FadeIn();
			}else {
				FadeOut();
			}
		});
		
		//update labels if input field is not null
		citySearch.setOnAction(e ->{
			if(!citySearch.getText().isBlank()) {
				cityName.setText(citySearch.getText().trim());
				cityTemp.setText("0.00");
			}
		});
		
		//start service
		Service service = new Service();
		service.setPeriod(Duration.seconds(10));
		service.start();
		
		
	}
	
	public void FadeIn() {
		//show input field in a pretty way
		citySearch.setVisible(true);
		
		FadeTransition ft = new FadeTransition(Duration.millis(500), citySearch);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.setInterpolator(Interpolator.LINEAR);
		
		TranslateTransition tp = new TranslateTransition(Duration.millis(500), citySearch);
		tp.setFromY(70);
		tp.setByY(10);
		tp.setInterpolator(Interpolator.LINEAR);
		
		ParallelTransition pt = new ParallelTransition(ft, tp);
		pt.setNode(citySearch);
		pt.play();
		
		textFieldVisibility = true;
	}
	
	public void FadeOut() {
		//hide the input field with style	
		FadeTransition ft = new FadeTransition(Duration.millis(500), citySearch);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.setInterpolator(Interpolator.LINEAR);
		
		TranslateTransition tp = new TranslateTransition(Duration.millis(500), citySearch);
		tp.setFromY(80);
		tp.setByY(-10);
		tp.setInterpolator(Interpolator.LINEAR);
		
		ParallelTransition pt = new ParallelTransition(ft, tp);
		pt.setNode(citySearch);
		pt.play();
		
		textFieldVisibility = false;
	}
	
	public double WeatherInfo(String city) {
		//get weather data from API
		String result = "";
		double weatherInfo = 0;
		
		try {
			//replace placeholder with your API key. Free to get @ openweathermap.org
			URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=imperial&APPID=YOURAPIKEYHERE");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream in = conn.getInputStream();
			InputStreamReader reader = new InputStreamReader(in);
			int data = reader.read();
			while(data != -1) {
				char current = (char) data;
				result += current;
				data = reader.read();
			}
			
			JSONObject jsonObject = new JSONObject(result);
			weatherInfo = jsonObject.getJSONObject("main").getDouble("temp");
			
			cityTemp.setText(String.valueOf(weatherInfo));
					
		}catch(Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("City Not Found!");
			alert.showAndWait();
		}
		
		return weatherInfo;
		
	}
	
	public void ColorTransition(Boolean hot) {
		//color transition for hot/cold weather
		fill = new FillTransition();
		fill.setAutoReverse(true);
		fill.setCycleCount(Timeline.INDEFINITE);
		if(hot) {
			fill.setFromValue(Color.rgb(255, 128, 0));
			fill.setToValue(Color.rgb(255, 153, 0));
			fill.setDuration(Duration.millis(1000));
		}else {
			fill.setFromValue(Color.rgb(0 ,128, 255));
			fill.setToValue(Color.rgb(51, 153, 255));
			fill.setDuration(Duration.millis(1000));
		}
		
		fill.setShape(status_background);
		fill.play();
	}
	private class Service extends ScheduledService<Boolean>{

		@Override
		protected Task<Boolean> createTask() {
			// TODO Auto-generated method stub
			return new Task<>() {

				@Override
				protected Boolean call() throws Exception {
					// TODO Auto-generated method stub
					if(isCancelled()) {
						return false;
					}
					//this will keep your program from crashing by running the service when resources allow
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Boolean hot = false;
							double currTemp = WeatherInfo(cityName.getText());
							if(currTemp > 70) {
								hot = true;
							}
							ColorTransition(hot);
						}
					});
					return true;
				}
				
			};
			
		}
		
	}
	
}
