package SparkML;

import java.util.List;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Prediction {

    public static void runAnalysis(SparkSession spark) {
        try {

            spark = SparkSession.builder().appName("Analytics").master("local[*]").getOrCreate();

            String dir_pipeline = "C:/Users/rndpo/OneDrive/Desktop/NetBeansProjects/SparkLMTest/spark_dropout_pipeline";
            String dir_model = "C:/Users/rndpo/OneDrive/Desktop/NetBeansProjects/SparkLMTest/spark_dropout_model";

            PipelineModel pipeline = PipelineModel.load(dir_pipeline);
            System.out.println("pipeline loaded");
            RandomForestClassificationModel model = RandomForestClassificationModel.load(dir_model);
            System.out.println("Classifier model loaded successfully.");
            

            // 3. Load current student data for prediction
            List<ParamDatasets> currentRecords = ParamDataLoader.fetchCurrentParams();
            Dataset<Row> rawCurrentData = ParamDataLoader.convertCurrentParams(spark, currentRecords);
            Dataset<Row> currentData = FeatureEngineer.transform(rawCurrentData);

            // 4. Apply model to current data
            Dataset<Row> predictions = pipeline.transform(currentData);
            Dataset<Row> probPrediction = model.transform(predictions);

            // Accuracy
            MulticlassClassificationEvaluator accuracyEval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("accuracy");

            double accuracy = accuracyEval.evaluate(probPrediction);
            System.out.println("✅ Accuracy: " + accuracy);

// Precision
            MulticlassClassificationEvaluator precisionEval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("weightedPrecision");

            double precision = precisionEval.evaluate(probPrediction);
            System.out.println("🎯 Precision: " + precision);

// Recall
            MulticlassClassificationEvaluator recallEval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("weightedRecall");

            double recall = recallEval.evaluate(probPrediction);
            System.out.println("📈 Recall: " + recall);

// F1 Score
            MulticlassClassificationEvaluator f1Eval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("f1");

            double f1 = f1Eval.evaluate(probPrediction);
            System.out.println("🏆 F1 Score: " + f1);

            // 5. Filter high-risk students (prediction == 1.0)
            Dataset<Row> highRiskStudents = probPrediction;

//            // 6. Display results in UI
            RiskTableRenderer.render(highRiskStudents);
//
//            // 7. Export to CSV
//            CSVExporter.export(highRiskStudents, "exports/high_risk_students.csv");
//
//            // 8. Optional: Notify stakeholders
//            NotificationService.notifyStakeholders(highRiskStudents);

        } catch (Exception e) {
            System.err.println("Error running dropout risk analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
