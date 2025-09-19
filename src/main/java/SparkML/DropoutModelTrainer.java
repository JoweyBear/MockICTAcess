package SparkML;

import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DropoutModelTrainer {

    public static PipelineModel train(Dataset<Row> trainingData) {
        // 1. Define the classifier
        RandomForestClassifier classifier = new RandomForestClassifier()
            .setLabelCol("label")
            .setFeaturesCol("features")
            .setPredictionCol("prediction")
            .setNumTrees(50); // You can tune this

        // 2. Build pipeline (just the classifier here, since features are already engineered)
        Pipeline pipeline = new Pipeline()
            .setStages(new PipelineStage[]{classifier});
        

        // 3. Train the model
        PipelineModel model = pipeline.fit(trainingData);

        return model;
    }
}
