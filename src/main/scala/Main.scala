import org.apache.spark.sql.SparkSession
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.category.DefaultCategoryDataset
import javax.swing.JFrame

object LogAnalysis {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("LogAnalysis")
      .master("local[*]")
      .getOrCreate()

    val logFile = "logs.txt"

    val logData = spark.read.textFile(logFile)

    import spark.implicits._

    // Extract response codes from log lines
    val responseCodes = logData.flatMap { line =>
      val pattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} ERROR (\\d{3})".r
      pattern.findFirstMatchIn(line).map(_.group(1))
    }

    // Count the occurrence of each response code
    val responseCodeCounts = responseCodes.groupBy($"value").count()

    // Filter response codes >= 400
    val filteredResponseCodeCounts = responseCodeCounts.filter($"value" >= 400)

    // Create a dataset for JFreeChart
    val dataset = new DefaultCategoryDataset()

    // Add response code counts to the dataset
    filteredResponseCodeCounts.collect().foreach { row =>
      dataset.addValue(row.getLong(1), "Errors", row.getString(0))
    }

    // Create a bar chart with JFreeChart
    val chart = ChartFactory.createBarChart(
      "Errors by HTTP Status Code",
      "HTTP Status Code",
      "Count",
      dataset
    )

    // Create a panel for the chart
    val chartPanel = new ChartPanel(chart)

    // Create a window to display the chart
    val frame = new JFrame()
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane().add(chartPanel)
    frame.pack()
    frame.setVisible(true)

    spark.stop()
  }
}
