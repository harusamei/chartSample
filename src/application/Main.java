package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
 
//画简单的图, 但是好象存不上啊，妈蛋不存了

public class Main extends Application {
	
	public static void main(String[] args) {
		System.out.println("this is draw pic");		
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws ParseException{
		
		multiChart(stage);
		 
	}
	
	public static void multiChart(Stage stage){
		
		final LineChart<Number,Number> sc = new LineChart<>(new NumberAxis(),new NumberAxis());

	    XYChart.Series series1 = new XYChart.Series();
	    series1.setName("Equities");
	    series1.getData().add(new XYChart.Data(4.2, 193.2));
	    series1.getData().add(new XYChart.Data(2.8, 33.6));
	    series1.getData().add(new XYChart.Data(6.8, 23.6));

	    XYChart.Series series2 = new XYChart.Series();
	    series2.setName("Mutual funds");
	    series2.getData().add(new XYChart.Data(5.2, 229.2));
	    series2.getData().add(new XYChart.Data(2.4, 37.6));
	    series2.getData().add(new XYChart.Data(6.4, 15.6));

	    sc.setAnimated(false);
	    sc.setCreateSymbols(true);

	    sc.getData().addAll(series1, series2);

	    Scene scene  = new Scene(sc, 500, 400);
	    //scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());
	    stage.setScene(scene);
	    stage.show();

	}
	public static void lineChartSample(Stage stage){
		
		    stage.setTitle("Line Chart Sample");
	        //defining the axes
	        final NumberAxis xAxis = new NumberAxis();
	        final NumberAxis yAxis = new NumberAxis();
	        xAxis.setLabel("Number of Month");
	        //creating the chart
	        final LineChart<Number,Number> lineChart = 
	                new LineChart<Number,Number>(xAxis,yAxis);
	                
	        lineChart.setTitle("Stock Monitoring, 2010");
	        //defining a series
	        XYChart.Series series = new XYChart.Series();
	        series.setName("My portfolio");
	        //populating the series with data
	        series.getData().add(new XYChart.Data(1, 23));
	        series.getData().add(new XYChart.Data(2, 14));
	        series.getData().add(new XYChart.Data(3, 15));
	        series.getData().add(new XYChart.Data(4, 24));
	        series.getData().add(new XYChart.Data(5, 34));
	        series.getData().add(new XYChart.Data(6, 36));
	        series.getData().add(new XYChart.Data(7, 22));
	        series.getData().add(new XYChart.Data(8, 45));
	        series.getData().add(new XYChart.Data(9, 43));
	        series.getData().add(new XYChart.Data(10, 17));
	        series.getData().add(new XYChart.Data(11, 29));
	        series.getData().add(new XYChart.Data(12, 25));
	        
	        Scene scene  = new Scene(lineChart,800,600);
	        lineChart.getData().add(series);
	       
	       
	        stage.setScene(scene);
	        stage.show();
	        
	}
	public static void Scatter(Stage stage,String DLR, String yStr) throws ParseException {
		
		stage.setTitle("Scatter Chart of order form");
		
        final NumberAxis xAxis = new NumberAxis(0, 500, 10);
        final NumberAxis yAxis = new NumberAxis(0, 500, 5);        
        final ScatterChart<Number,Number> sc = new
            ScatterChart<Number,Number>(xAxis,yAxis);
        
        xAxis.setLabel("day in "+yStr);                
        yAxis.setLabel("order quantity");
        sc.setTitle("order Overview of "+DLR);
        
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("order");
        
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("empty");

        MongoCollection<Document> aTable=null;
		MongoClient mClient=null;		
        mClient=MongoDBHelper.getMongoDbClient("localhost", 27017, "root", "root", "admin");
		aTable=MongoDBHelper.getMongoDbCollection(mClient,"toyota" , "orderStats");
		Document tObj=null;
		
		Bson tCond=null;		
		tCond=Filters.and(Filters.eq("year",yStr),Filters.eq("DLRCODE",DLR));
		FindIterable<Document> findIt = aTable.find(tCond);	//直接find() 全部遍历
		MongoCursor<Document> aCursor = findIt.noCursorTimeout(true).iterator();
		
		Date today= new Date();
		Calendar curDay=Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		int [] dayGoods=new int[366];
		Arrays.fill(dayGoods,0);
		
		int x, y;
		int count=0;
		while(aCursor.hasNext()){
			tObj=aCursor.next();
			today=tObj.getDate("Date");
			curDay.setTime(today);
			x=curDay.get(Calendar.DAY_OF_YEAR);
			y=tObj.getInteger("sumOfOrder");
			dayGoods[x]++;
			if(y>459) y=494;
			series1.getData().add(new XYChart.Data(x, y));
			System.out.println(x+"-"+y);
			count++;
		}
		System.out.println(count);

		
		today=df.parse(yStr+"0101");
		curDay.setTime(today);
		count=0;
		for(int i=1; i<366; i++){
			curDay.roll(Calendar.DAY_OF_YEAR, 1);
			if(dayGoods[i]>0) continue;
			count++;
			System.out.println(curDay.getTime().toString());
			series2.getData().add(new XYChart.Data(i, 3));
		}
		System.out.println(count);
		sc.getData().addAll(series1,series2);
        Scene scene  = new Scene(sc, 1000, 800);
        stage.setScene(scene);
        stage.show();
	}
	public static void ScatterChartSample (Stage stage){
		
		 stage.setTitle("Scatter Chart Sample");
	        final NumberAxis xAxis = new NumberAxis(0, 10, 1);
	        final NumberAxis yAxis = new NumberAxis(-100, 500, 100);        
	        final ScatterChart<Number,Number> sc = new
	            ScatterChart<Number,Number>(xAxis,yAxis);
	        xAxis.setLabel("Age (years)");                
	        yAxis.setLabel("Returns to date");
	        sc.setTitle("Investment Overview");
	       
	        XYChart.Series series1 = new XYChart.Series();
	        series1.setName("Equities");
	        series1.getData().add(new XYChart.Data(4.2, 193.2));
	        series1.getData().add(new XYChart.Data(2.8, 33.6));
	        series1.getData().add(new XYChart.Data(6.2, 24.8));
	        series1.getData().add(new XYChart.Data(1, 14));
	        series1.getData().add(new XYChart.Data(1.2, 26.4));
	        series1.getData().add(new XYChart.Data(4.4, 114.4));
	        series1.getData().add(new XYChart.Data(8.5, 323));
	        series1.getData().add(new XYChart.Data(6.9, 289.8));
	        series1.getData().add(new XYChart.Data(9.9, 287.1));
	        series1.getData().add(new XYChart.Data(0.9, -9));
	        series1.getData().add(new XYChart.Data(3.2, 150.8));
	        series1.getData().add(new XYChart.Data(4.8, 20.8));
	        series1.getData().add(new XYChart.Data(7.3, -42.3));
	        series1.getData().add(new XYChart.Data(1.8, 81.4));
	        series1.getData().add(new XYChart.Data(7.3, 110.3));
	        series1.getData().add(new XYChart.Data(2.7, 41.2));
	        
	        XYChart.Series series2 = new XYChart.Series();
	        series2.setName("Mutual funds");
	        series2.getData().add(new XYChart.Data(5.2, 229.2));
	        series2.getData().add(new XYChart.Data(2.4, 37.6));
	        series2.getData().add(new XYChart.Data(3.2, 49.8));
	        series2.getData().add(new XYChart.Data(1.8, 134));
	        series2.getData().add(new XYChart.Data(3.2, 236.2));
	        series2.getData().add(new XYChart.Data(7.4, 114.1));
	        series2.getData().add(new XYChart.Data(3.5, 323));
	        series2.getData().add(new XYChart.Data(9.3, 29.9));
	        series2.getData().add(new XYChart.Data(8.1, 287.4));
	 
	        sc.getData().addAll(series1, series2);
	        Scene scene  = new Scene(sc, 500, 400);
	        stage.setScene(scene);
	        stage.show();
	}
	
}
