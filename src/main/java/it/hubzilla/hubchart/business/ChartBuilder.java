package it.hubzilla.hubchart.business;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;

public class ChartBuilder {

	private static final Color BRAND_COLOUR = new Color(67, 72, 138);//43488A
	private static final Color LIGHTGRAY = new Color(240, 240, 240);
	
	//private Series totalChannels = null;
	List<Long> actChanTime = new ArrayList<Long>();
	List<Integer> actChanData = new ArrayList<Integer>();
	private int width;
	private int height;
	
	public ChartBuilder(int width, int height) {
		this.width=width;
		this.height=height;
	}
	
	public void addChartValue(Date time, Integer value) {
		actChanTime.add(time.getTime());
		actChanData.add(value);
	}
	
	
	public byte[] drawPngChart() throws IOException {
		Chart chart = new Chart(width, height);
		chart.getStyleManager().setAxisTicksLineVisible(false);
		chart.getStyleManager().setAxisTicksMarksVisible(false);
		//chart.getStyleManager().setAxisTicksVisible(true);
		chart.getStyleManager().setAxisTitlesVisible(false);
		chart.getStyleManager().setChartTitleBoxVisible(false);
		chart.getStyleManager().setChartTitleVisible(false);
		chart.getStyleManager().setChartBackgroundColor(Color.WHITE);
		chart.getStyleManager().setLegendVisible(false);
		chart.getStyleManager().setPlotBackgroundColor(LIGHTGRAY);
		chart.getStyleManager().setPlotBorderColor(Color.WHITE);
		chart.getStyleManager().setPlotBorderVisible(false);
		chart.getStyleManager().setPlotGridLinesVisible(false);
		chart.getStyleManager().setPlotPadding(0);
		chart.getStyleManager().setPlotTicksMarksVisible(false);
		chart.getStyleManager().setXAxisTicksVisible(false);
		chart.getStyleManager().setYAxisTicksVisible(true);
		
		chart.getStyleManager().setDecimalPattern("#0.#");
		
		//Active channels
		Series activeChannels = chart.addSeries("active channels", actChanTime, actChanData);
		activeChannels.setLineStyle(SeriesLineStyle.SOLID);
		activeChannels.setLineColor(BRAND_COLOUR);
		activeChannels.setMarker(SeriesMarker.NONE);
		
		byte[] imageBytes = BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
		return imageBytes;
	}
}
