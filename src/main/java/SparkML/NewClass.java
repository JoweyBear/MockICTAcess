/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SparkML;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.api.java.UDF2;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;
import org.apache.spark.sql.types.DataTypes;

/**
 *
 * @author rndpo
 */
public class NewClass {
    
//        PipelineModel pipeline = PipelineModel.load(dir_pipeline);
//        RandomForestClassificationModel model = RandomForestClassificationModel.load(dir_model);
//        System.out.println("Classifier model loaded successfully.");
//        System.out.println("pipeline loaded");
//        Dataset<Row> df = spark.read()
//                .option("header", "true")
//                .csv("C:/Users/rndpo/Downloads/new_test_datasets.csv");
//        df.printSchema();
//
//        df = df
//                .withColumn("age", df.col("age").cast("int"))
//                .withColumn("birth order", df.col("birth order").cast("int"))
//                .withColumn("family income", df.col("family income").cast("int"))
//                .withColumn("admissionGrade", df.col("admissionGrade").cast("int"))
//                .withColumn("curricular units passed", df.col("curricular units passed").cast("int"))
//                .withColumn("curricular units failed", df.col("curricular units failed").cast("int"))
//                .withColumn("year level", df.col("year level").cast("int"))
//                .withColumn("late counts", df.col("late counts").cast("int"))
//                .withColumn("gpa", df.col("gpa").cast("double"))
//                .withColumn("absents counts", df.col("absents counts").cast("int"))
//                .withColumn("failed subjects", df.col("failed subjects").cast("int"));
//
//        StringIndexerModel labelIndexerModel = null; // Use StringIndexerModel after fitting
//        for (Transformer stage : pipeline.stages()) {
//            // Check if the stage is a StringIndexerModel (since the pipeline is fitted)
//            if (stage instanceof StringIndexerModel) {
//                StringIndexerModel currentStage = (StringIndexerModel) stage;
//                // Check if the output column of this StringIndexerModel is "label"
//                if ("label".equals(currentStage.getOutputCol())) {
//                    labelIndexerModel = currentStage;
//                    break;
//                }
//            }
//        }
//
//        Dataset<Row> predictions = pipeline.transform(df);
//        Dataset<Row> prediction = model.transform(predictions);
////        prediction.printSchema();
//        if (labelIndexerModel != null) {
//            // Create an IndexToString transformer using the labels from the found StringIndexerModel
//            IndexToString labelConverter = new IndexToString()
//                    .setInputCol("prediction") // Input column is the numerical prediction
//                    .setOutputCol("predictedLabel") // Output column for the converted string label
//                    .setLabels(labelIndexerModel.labels()); // Get labels from the StringIndexerModel
//
//            // Apply the IndexToString transformer to your predictions DataFrame
////            Dataset<Row> finalPrediction = labelConverter.transform(prediction);
//            UDF2<Vector, Double, String> extractProbability = (vector, predictionInt) -> {
//                int index = predictionInt.intValue();
//                double prob = vector.apply(index);
//                return String.format("%.2f%%", prob * 100);
//            };
//
//// Register the UDF
//            spark.udf().register("getProbabilityPercent", extractProbability, DataTypes.StringType);
//
//// Apply it to your DataFrame
//            Dataset<Row> finalPrediction = labelConverter.transform(prediction)
//                    .withColumn("dropoutProbability", callUDF("getProbabilityPercent", col("probability"), col("prediction")));
//
//            // Show prediction result with converted label
//            System.out.println("\nPrediction Results (with converted labels):");
//            finalPrediction.select("studentId", "prediction", "predictedLabel", "probability", "dropoutProbability").show();
//        } else {
//            System.out.println("Could not find the label indexer stage in the pipeline.");
//            // Show prediction result without converted label if indexer not found
//            System.out.println("\nPrediction Results (raw):");
//            predictions.select("studentId", "prediction", "probability").show();
//        }
}
