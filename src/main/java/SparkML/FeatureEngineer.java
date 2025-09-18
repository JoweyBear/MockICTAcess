package SparkML;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class FeatureEngineer {

    public static Dataset<Row> transform(Dataset<Row> rawData) {
        // 1. Index categorical columns
        StringIndexer academicIndexer = new StringIndexer()
            .setInputCol("academicStatus")
            .setOutputCol("academicIndex");
        
        StringIndexer admissionIndexer = new StringIndexer()
                .setInputCol("admissionType")
                .setOutputCol("admissionIndex");
                
        StringIndexer scholarshipIndexer = new StringIndexer()
                .setInputCol("scholarshipStatus")
                .setOutputCol("scholarshipIndex");
        
        StringIndexer parentIndexer = new StringIndexer()
                .setInputCol("parentEducation")
                .setOutputCol("parentEduIndex");
        
        StringIndexer transportIndexer = new StringIndexer()
            .setInputCol("transportMode")
            .setOutputCol("transportIndex");

        StringIndexer labelIndexer = new StringIndexer()
            .setInputCol("dropoutStatus")
            .setOutputCol("label"); // Only used during training

        // 2. Assemble numerical + indexed features
        VectorAssembler assembler = new VectorAssembler()
            .setInputCols(new String[]{
                "gpa", "failedSubjects", "curricularUnitsPassed", "curricularUnitsFailed",
                "lateCounts", "absentCounts", "familyIncome",
                "academicIndex", "transportIndex", "admissionIndex", "scholarshipIndex", "parentEduIndex"
            })
            .setOutputCol("features");

        // 3. Build pipeline
        Pipeline pipeline = new Pipeline()
            .setStages(new PipelineStage[]{academicIndexer, admissionIndexer, scholarshipIndexer, parentIndexer, transportIndexer, labelIndexer, assembler});

        // 4. Transform data
        PipelineModel model = pipeline.fit(rawData);
        return model.transform(rawData);
    }
}
