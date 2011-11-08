package edu.gemini.aspen.gpi.web.ui.aoplot

import com.vaadin.Application;
import java.util.logging.Logger

import com.vaadin.ui.themes.BaseTheme
import com.vaadin.ui.TabSheet.SelectedTabChangeListener
import com.invient.vaadin.charts.InvientChartsConfig.GeneralChartConfig.{ZoomType, Margin}
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.DateTimePlotBand.DateTimeRange
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.{NumberPlotLine, Tick, AxisTitle, DateTimePlotBand}
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.NumberPlotLine.NumberValue
import com.invient.vaadin.charts.Color.{RGB, RGBA}
import com.invient.vaadin.charts.InvientCharts.{DateTimePoint, DateTimeSeries, SeriesType}
import java.util.{Calendar, Date}
import scala.collection.JavaConversions._
import collection.mutable.LinkedHashSet
import java.io.Serializable
import com.vaadin.terminal.{Sizeable, ClassResource}
import org.apache.felix.ipojo.annotations.{Invalidate, Requires, Bind, Unbind}
import org.vaadin.artur.icepush.ICEPush
import com.invient.vaadin.charts.{InvientCharts, InvientChartsConfig}
import com.invient.vaadin.charts.InvientChartsConfig.{Tooltip, NumberYAxis, DateTimeAxis}
import com.vaadin.data.util.{HierarchicalContainer, ObjectProperty}
import com.vaadin.ui._
import java.lang.{String, Boolean}
import com.vaadin.ui.Button.ClickListener
import com.vaadin.data.Item

/**
 * Main page of the GDS web UI
 */
@org.apache.felix.ipojo.annotations.Component(name = "GPIAOFactory")
class GPIAOVaadinApp extends Application {
  private val LOG = Logger.getLogger(this.getClass.getName)
  val STREHL = "Strehl"
  val R0 = "R0"
  val NAME_PROPERTY = "NAME"
  val VALUE_PROPERTY = "VALUE"
  val GRAPH_PROPERTY = "GRAPH"

  val AOC_STATS = "AOC Statistics"

  val NO_MARKER = new InvientChartsConfig.Marker {
    override def getEnabled = false

    override def setEnabled(enabled: Boolean) {}
  }
  val pointConfig = new InvientChartsConfig.PointConfig(NO_MARKER)
  val mainWindow = new Window("GPI AO")
  val strehlSeriesData = new DateTimeSeries(STREHL, true)
  val r0SeriesData = new DateTimeSeries(R0, true)
  val xAxis = new DateTimeAxis()
    // Add Table columns
    val dataSource = new HierarchicalContainer

    dataSource.addContainerProperty(NAME_PROPERTY, classOf[String], "")
    dataSource.addContainerProperty(VALUE_PROPERTY, classOf[String], "")
    dataSource.addContainerProperty(GRAPH_PROPERTY, classOf[CheckBox], null)
    val aocStatistics = dataSource.addItem(AOC_STATS)
    val aocStatR0 = dataSource.addItem(R0)
    val aocStatStrehl = dataSource.addItem(STREHL)

  val chart = buildChart()
  val strehlSimulator = new DataSimulatorActor(chart, strehlSeriesData, aocStatStrehl, (start:Long, last:Double) => {
    val update = 1 / (1 + scala.math.exp(-(System.currentTimeMillis() - start) / 10e3)) + (scala.math.random - 0.5) / 100
    scala.math.floor(update * 100) / 100
  })
  val r0Simulator = new DataSimulatorActor(chart, r0SeriesData, aocStatR0, (start:Long, last:Double) => {
    val update = last + 0.001*scala.math.sqrt((System.currentTimeMillis() - start)*scala.math.random / 10)
    scala.math.floor(update* 100) / 100
    0.2
  })
  val pusher = new ICEPush()

  /**
   * Called by Vaadin when the application needs to start
   *
   * In this case we layout the main components
   */
  override def init() {
    LOG.info("GPIAOVaadinApp init> ")
    val dataPanelLayout = new VerticalLayout

    chart.setSizeFull()

    val mainLayout = new HorizontalSplitPanel
    mainLayout.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE)

    /*val indicator = new ProgressIndicator {
      0.2f
    }
    indicator.setPollingInterval(5000)
    indicator.setStyleName("i-progressindicator-invisible")*/

    val treetable = new TreeTable()
    treetable.setSizeFull()
    aocStatStrehl.getItemProperty(VALUE_PROPERTY).setValue("0.00")
    aocStatStrehl.getItemProperty(NAME_PROPERTY).setValue(STREHL)
    val strehlCheck = new CheckBox("Shown")
    strehlCheck.setValue(true)
    strehlCheck.addListener(new ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        if (strehlCheck.getValue == false) {
          strehlCheck.setCaption("Hidden")
          chart.getSeries(STREHL).hide()
        } else {
          strehlCheck.setCaption("Shown")
          chart.getSeries(STREHL).show()
        }
      }
    })
    aocStatStrehl.getItemProperty(GRAPH_PROPERTY).setValue(strehlCheck)

    aocStatR0.getItemProperty(VALUE_PROPERTY).setValue("0.00")
    aocStatR0.getItemProperty(NAME_PROPERTY).setValue(R0)
    val r0Check = new CheckBox("Hidden")
    r0Check.setValue(false)
    r0Check.addListener(new ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        if (r0Check.getValue == false) {
          r0Check.setCaption("Hidden")
          chart.getSeries(R0).hide()
        } else {
          r0Check.setCaption("Shown")
          chart.getSeries(R0).show()
        }
      }
    })

    aocStatR0.getItemProperty(GRAPH_PROPERTY).setValue(r0Check)

    aocStatistics.getItemProperty(NAME_PROPERTY).setValue(AOC_STATS)

    dataSource.setParent(STREHL, AOC_STATS)
    dataSource.setParent(R0, AOC_STATS)
    dataSource.setChildrenAllowed(STREHL, false)
    dataSource.setChildrenAllowed(R0, false)
    
    treetable.setContainerDataSource(dataSource)

    dataPanelLayout.addComponent(treetable)
    dataPanelLayout.addComponent(pusher)
    treetable.setCollapsed(AOC_STATS, false)

    mainLayout.setFirstComponent(dataPanelLayout)
    mainLayout.setSecondComponent(chart)

    mainLayout.setSizeFull

    mainWindow.setContent(mainLayout)
    mainWindow.setCaption("AO Performance")
    setMainWindow(mainWindow)
    //splineThread.start()
  }

  def buildChart() = {
    val chartConfig = new InvientChartsConfig()
    chartConfig.getGeneralChartConfig().setType(SeriesType.LINE)
    chartConfig.getGeneralChartConfig().setMargin(new Margin())
    chartConfig.getGeneralChartConfig().getMargin().setRight(10)
    //chartConfig.getGeneralChartConfig().setZoomType(ZoomType.NONE)
    chartConfig.getGeneralChartConfig().setReflow(true)
    chartConfig.getGeneralChartConfig().setIgnoreHiddenSeries(false)
    val strehlSeriesConfig = new InvientChartsConfig.SeriesConfig()
    strehlSeriesConfig.setShadow(false)
    strehlSeriesConfig.setShowCheckbox(false)
    strehlSeriesConfig.setEnableMouseTracking(false)
    strehlSeriesConfig.setVisible(false)
    val seriesConfig = new java.util.LinkedHashSet[InvientChartsConfig.SeriesConfig]()
    seriesConfig.add(strehlSeriesConfig)
    //chartConfig.setSeriesConfig(seriesConfig)

    chartConfig.getTitle().setText("Live random values")


    xAxis.setTick(new Tick())
    //xAxis.getTick().setPixelInterval(150)
    //xAxis.setMin(new Date())
    val xAxes = new java.util.LinkedHashSet[InvientChartsConfig.XAxis]()
    xAxes.add(xAxis)
    chartConfig.setXAxes(xAxes)

    val yAxis = new NumberYAxis()
    yAxis.setTitle(new AxisTitle("Value"))
    //yAxis.setMax(1.2)
    yAxis.setMaxPadding(0.5)
    yAxis.setMinPadding(0.5)
    //yAxis.setMin(0.0)
    /*val plotLine = new NumberPlotLine("LineAt0")
    yAxis.addPlotLine(plotLine)
    plotLine.setValue(new NumberValue(0.0))
    plotLine.setWidth(1)
    plotLine.setColor(new RGB(128, 128, 128))*/
    val yAxes = new java.util.LinkedHashSet[InvientChartsConfig.YAxis]()
    yAxes.add(yAxis)
    chartConfig.setYAxes(yAxes)

    chartConfig
      .getTooltip()
      .setFormatterJsFunc(
      "function() {"
        + " return '<b>'+ this.series.name +'</b><br/>'+ "
        + " $wnd.Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+ "
        + " $wnd.Highcharts.numberFormat(this.y, 2);"
        + "}");

    chartConfig.getLegend().setEnabled(true)
    chartConfig.getCredit().setEnabled(false)
    chartConfig.setTooltip(new InvientChartsConfig.Tooltip())
    chartConfig.getGeneralChartConfig.setAnimation(false)

    val chart = new InvientCharts(chartConfig)

    val points = new java.util.LinkedHashSet[InvientCharts.DateTimePoint]()
    val dtNow = new Date();
    // Add random data.
    for (i <- -150 to 0) {
      val point = new DateTimePoint(strehlSeriesData, getUpdatedDate(dtNow,
        i * 1000), 0.0)
      point.setConfig(pointConfig)
      points.add(point)
    }

    strehlSeriesData.setSeriesPoints(points)
    chart.addSeries(strehlSeriesData)

    {
      val points = new java.util.LinkedHashSet[InvientCharts.DateTimePoint]()
      val dtNow = new Date();
      // Add random data.
      for (i <- -150 to 0) {
        val point = new DateTimePoint(r0SeriesData, getUpdatedDate(dtNow,
          i * 1000), 0)
        points.add(point)
        point.setConfig(pointConfig)
      }
      r0SeriesData.setSeriesPoints(points)
      chart.addSeries(r0SeriesData)

    }
    chart
  }

  @Invalidate
  def stopListening() {
    strehlSimulator.stop()
  }

  def getUpdatedDate(dt: Date, milliseconds: Long): Date = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(dt.getTime() + milliseconds)
    cal.getTime()
  }

  class DataSimulatorActor(chart: InvientCharts, seriesData: DateTimeSeries, item:Item, simulator:(Long, Double) => Double) {
    var last = 0.0
    val start = System.currentTimeMillis()

    import actors.{Actor, TIMEOUT}
    import actors.Actor._

    def scheduler(time: Long)(f: => Unit) = {
      def fixedRateLoop {
        Actor.reactWithin(time) {
          case TIMEOUT => f; fixedRateLoop
          case 'stop =>
        }
      }
      Actor.actor(fixedRateLoop)
    }

    val timerActor = actor {
      loop {
        receiveWithin(3000) {
          case TIMEOUT => println("TIMEOUT")
          case 'generate => {
            last = simulator(start, last)
            //println("Update " + seriesData.toString + " => " + last)
            val point = new DateTimePoint(seriesData,
              new Date(), last)
            point.setConfig(pointConfig)
            seriesData.addPoint(point, true)
            item.getItemProperty(VALUE_PROPERTY).setValue("" + last)
            //chart.removeSeries(seriesData)
            //chart.addSeries(seriesData)
            //chart.refresh()
            pusher.push()
          }
          case 'stop => exit
        }
      }
    }
    scheduler(2000) {
      timerActor ! 'generate
    }

    def stop() {
      timerActor ! 'stop
    }
  }

}