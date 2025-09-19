package SparkML;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.spark.sql.SparkSession;

public class ModelManager {

    private static final String MODEL_PATH = "models/dropout-risk-model";

    public static boolean modelExists() {
        return new File(MODEL_PATH).exists();
    }

    public static void saveModel(PipelineModel model) {
        try {
            model.write().overwrite().save(MODEL_PATH);
        } catch (IOException ex) {
            Logger.getLogger(ModelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PipelineModel loadModel() {
        return PipelineModel.load(MODEL_PATH);
    }

    public static PipelineModel getOrTrainModel(SparkSession spark, Dataset<Row> trainingData) {
        if (modelExists()) {
            System.out.println("✅ Model found. Loading existing model...");
            return loadModel();
        } else {
            System.out.println("⚙️ No model found. Training new model...");
            PipelineModel model = DropoutModelTrainer.train(trainingData);
            saveModel(model);
            System.out.println("💾 Model trained and saved.");
            return model;
        }
    }
}
