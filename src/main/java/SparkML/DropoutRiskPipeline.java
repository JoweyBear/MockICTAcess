package SparkML;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import java.util.List;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;

public class DropoutRiskPipeline {

    public static void runAnalysis(SparkSession spark) {
        try {
            // 1. Load historical data for training
            List<ParamDatasets> historicalRecords = ParamDataLoader.fetchParams();
            Dataset<Row> rawTrainingData = ParamDataLoader.convertHistoryParams(spark, historicalRecords);
            Dataset<Row> trainingData = FeatureEngineer.transform(rawTrainingData);

            // 2. Train or load model
            PipelineModel model = ModelManager.getOrTrainModel(spark, trainingData);

            // 3. Load current student data for prediction
            List<ParamDatasets> currentRecords = ParamDataLoader.fetchCurrentParams();
            Dataset<Row> rawCurrentData = ParamDataLoader.convertCurrentParams(spark, currentRecords);
            Dataset<Row> currentData = FeatureEngineer.transform(rawCurrentData);

            // 4. Apply model to current data
            Dataset<Row> predictions = model.transform(currentData);

            // Accuracy
            MulticlassClassificationEvaluator accuracyEval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("accuracy");

            double accuracy = accuracyEval.evaluate(predictions);
            System.out.println("✅ Accuracy: " + accuracy);

// Precision
            MulticlassClassificationEvaluator precisionEval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("weightedPrecision");

            double precision = precisionEval.evaluate(predictions);
            System.out.println("🎯 Precision: " + precision);

// Recall
            MulticlassClassificationEvaluator recallEval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("weightedRecall");

            double recall = recallEval.evaluate(predictions);
            System.out.println("📈 Recall: " + recall);

// F1 Score
            MulticlassClassificationEvaluator f1Eval = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName("f1");

            double f1 = f1Eval.evaluate(predictions);
            System.out.println("🏆 F1 Score: " + f1);

            // 5. Filter high-risk students (prediction == 1.0)
            Dataset<Row> highRiskStudents = predictions.filter("prediction == 1.0");

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
