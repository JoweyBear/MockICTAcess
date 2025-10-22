package SparkML;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.when;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class FeatureEngineering {

    // Define ordering maps as static final members
    private static final Map<String, Double> VULNERABLE_GROUP_ORDER = new HashMap<>();
    private static final Map<String, Double> PRIMARY_SUPPORT_ORDER = new HashMap<>();
    private static final Map<String, Double> SOURCE_OF_INCOME_ORDER = new HashMap<>();
    private static final Map<String, Double> ANNUAL_FAMILY_INCOME_ORDER = new HashMap<>();

    static {
        // Initialize the ordering maps in a static initializer block
        VULNERABLE_GROUP_ORDER.put("solo_parent", 4.0);
        VULNERABLE_GROUP_ORDER.put("has_special_needs", 3.0);
        VULNERABLE_GROUP_ORDER.put("has_disability", 3.0);
        VULNERABLE_GROUP_ORDER.put("indigenous_person", 2.0);
        VULNERABLE_GROUP_ORDER.put("none", 0.0);
        VULNERABLE_GROUP_ORDER.put("Other", 0.0); // Default category

        PRIMARY_SUPPORT_ORDER.put("self_finance", 4.0);
        PRIMARY_SUPPORT_ORDER.put("parents", 2.0);
        PRIMARY_SUPPORT_ORDER.put("other_scholarship", 1.0);
        PRIMARY_SUPPORT_ORDER.put("unifast", 0.0); // Use the value from your Python notebook

        SOURCE_OF_INCOME_ORDER.put("business", 3.0);
        SOURCE_OF_INCOME_ORDER.put("wage", 2.0); // Use the value from your Python notebook
        SOURCE_OF_INCOME_ORDER.put("salary", 1.0); // Use the value from your Python notebook

        ANNUAL_FAMILY_INCOME_ORDER.put("below_100,000", 6.0);
        ANNUAL_FAMILY_INCOME_ORDER.put("100,000-249,999", 5.0);
        ANNUAL_FAMILY_INCOME_ORDER.put("250,000-499,999", 4.0);
        ANNUAL_FAMILY_INCOME_ORDER.put("500,000-749,999", 3.0);
        ANNUAL_FAMILY_INCOME_ORDER.put("750,000-999,999", 2.0);
        ANNUAL_FAMILY_INCOME_ORDER.put("1,000,000-1,499,999", 1.0);
        ANNUAL_FAMILY_INCOME_ORDER.put("1,500,000_above", 0.0);
    }

    /**
     * Applies manual ordered feature engineering to the input DataFrame.
     * Creates new columns: vulnerable_group_ordered, source_of_income_ordered,
     * primary_support_ordered, and annual_family_income_ordered.
     *
     * @param df The input Spark DataFrame.
     * @return A new DataFrame with the added ordered features.
     */
    public static Dataset<Row> applyManualOrderedFeatures(Dataset<Row> df) {
        Dataset<Row> dfWithOrderedFeatures = df;
        StructType schema = df.schema();
        String[] columns = df.columns();

        // vulnerable_group_ordered
        Column vulnerableGroupExprJava = null;
        boolean first = true;
        for (Map.Entry<String, Double> entry : VULNERABLE_GROUP_ORDER.entrySet()) {
            if (first) {
                vulnerableGroupExprJava = when(col("vulnerable_group_flag").equalTo(entry.getKey()), lit(entry.getValue()));
                first = false;
            } else {
                vulnerableGroupExprJava = vulnerableGroupExprJava.when(col("vulnerable_group_flag").equalTo(entry.getKey()), lit(entry.getValue()));
            }
        }
        if (vulnerableGroupExprJava != null) {
            vulnerableGroupExprJava = vulnerableGroupExprJava.otherwise(lit(VULNERABLE_GROUP_ORDER.getOrDefault("Other", 0.0))); // Default
        } else {
            vulnerableGroupExprJava = lit(VULNERABLE_GROUP_ORDER.getOrDefault("Other", 0.0)); // Just the default
        }

        if (Arrays.asList(columns).contains("vulnerable_group_flag")) {
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("vulnerable_group_ordered", vulnerableGroupExprJava.cast(DataTypes.DoubleType));
        } else {
            System.out.println("Warning: Column 'vulnerable_group_flag' not found. Adding 'vulnerable_group_ordered' with default value.");
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("vulnerable_group_ordered", lit(VULNERABLE_GROUP_ORDER.getOrDefault("Other", 0.0)).cast(DataTypes.DoubleType));
        }

        // source_of_income_ordered
        Column sourceOfIncomeExprJava = null;
        first = true;
        for (Map.Entry<String, Double> entry : SOURCE_OF_INCOME_ORDER.entrySet()) {
            if (first) {
                sourceOfIncomeExprJava = when(col("source_of_income").equalTo(entry.getKey()), lit(entry.getValue()));
                first = false;
            } else {
                sourceOfIncomeExprJava = sourceOfIncomeExprJava.when(col("source_of_income").equalTo(entry.getKey()), lit(entry.getValue()));
            }
        }
        if (sourceOfIncomeExprJava != null) {
            sourceOfIncomeExprJava = sourceOfIncomeExprJava.otherwise(lit(SOURCE_OF_INCOME_ORDER.getOrDefault("Other", 0.0))); // Default
        } else {
            sourceOfIncomeExprJava = lit(SOURCE_OF_INCOME_ORDER.getOrDefault("Other", 0.0)); // Just the default
        }
        if (Arrays.asList(columns).contains("source_of_income")) {
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("source_of_income_ordered", sourceOfIncomeExprJava.cast(DataTypes.DoubleType));
        } else {
            System.out.println("Warning: Column 'source_of_income' not found. Adding 'source_of_income_ordered' with default value.");
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("source_of_income_ordered", lit(SOURCE_OF_INCOME_ORDER.getOrDefault("Other", 0.0)).cast(DataTypes.DoubleType));
        }

        // primary_support_ordered
        Column primarySupportExprJava = null;
        first = true;
        for (Map.Entry<String, Double> entry : PRIMARY_SUPPORT_ORDER.entrySet()) {
            if (first) {
                primarySupportExprJava = when(col("primary_support_for_education").equalTo(entry.getKey()), lit(entry.getValue()));
                first = false;
            } else {
                primarySupportExprJava = primarySupportExprJava.when(col("primary_support_for_education").equalTo(entry.getKey()), lit(entry.getValue()));
            }
        }
        if (primarySupportExprJava != null) {
            primarySupportExprJava = primarySupportExprJava.otherwise(lit(PRIMARY_SUPPORT_ORDER.getOrDefault("Other", 0.0))); // Default
        } else {
            primarySupportExprJava = lit(PRIMARY_SUPPORT_ORDER.getOrDefault("Other", 0.0)); // Just the default
        }

        if (Arrays.asList(columns).contains("primary_support_for_education")) {
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("primary_support_ordered", primarySupportExprJava.cast(DataTypes.DoubleType));
        } else {
            System.out.println("Warning: Column 'primary_support_for_education' not found. Adding 'primary_support_ordered' with default value.");
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("primary_support_ordered", lit(PRIMARY_SUPPORT_ORDER.getOrDefault("Other", 0.0)).cast(DataTypes.DoubleType));
        }

        // annual_family_income_ordered (Handle based on expected type - assuming string for ordering)
        Column annualFamilyIncomeExprJava = null;
        first = true;
        // Check if the column exists and is a StringType for manual ordering
        if (Arrays.asList(columns).contains("annual_family_income")) {
            if (schema.apply("annual_family_income").dataType().equals(DataTypes.StringType)) {
                System.out.println("Applying manual ordering for annual_family_income (string type detected).");
                for (Map.Entry<String, Double> entry : ANNUAL_FAMILY_INCOME_ORDER.entrySet()) {
                    if (first) {
                        annualFamilyIncomeExprJava = when(col("annual_family_income").equalTo(entry.getKey()), lit(entry.getValue()));
                        first = false;
                    } else {
                        annualFamilyIncomeExprJava = annualFamilyIncomeExprJava.when(col("annual_family_income").equalTo(entry.getKey()), lit(entry.getValue()));
                    }
                }
                if (annualFamilyIncomeExprJava != null) {
                    annualFamilyIncomeExprJava = annualFamilyIncomeExprJava.otherwise(lit(ANNUAL_FAMILY_INCOME_ORDER.getOrDefault("Other", 0.0))); // Default
                } else {
                    annualFamilyIncomeExprJava = lit(ANNUAL_FAMILY_INCOME_ORDER.getOrDefault("Other", 0.0)); // Just the default
                }
                dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("annual_family_income_ordered", annualFamilyIncomeExprJava.cast(DataTypes.DoubleType));

            } else {
                System.out.println("annual_family_income is not a string type. Assuming it's numerical and using directly or requires different handling.");
                // If it's already numerical and the assembler expects "annual_family_income_ordered", you might need to rename the column:
                // if (Arrays.asList(columns).contains("annual_family_income")) {
                //     dfWithOrderedFeatures = dfWithOrderedFeatures.withColumnRenamed("annual_family_income", "annual_family_income_ordered");
                // }
                // Otherwise, if the assembler expects a different column name or type, adjust accordingly.
                // For this template, if not a string, we won't apply string ordering. Ensure the assembler
                // in the saved pipeline can handle the numerical column if it's present.
                // If the assembler *requires* "annual_family_income_ordered" as DoubleType,
                // and the original column is numerical, you might need to add a dummy column
                // or adjust the assembler definition in your Colab notebook and resave.
                if (!Arrays.asList(columns).contains("annual_family_income_ordered")) {
                    // Add a default column if the expected ordered column doesn't exist after checking
                    dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("annual_family_income_ordered", lit(ANNUAL_FAMILY_INCOME_ORDER.getOrDefault("Other", 0.0)).cast(DataTypes.DoubleType));
                }
            }
        } else {
            System.out.println("Warning: annual_family_income column not found. Adding 'annual_family_income_ordered' with default value.");
            // Add the column with default value if it's missing
            dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("annual_family_income_ordered", lit(ANNUAL_FAMILY_INCOME_ORDER.getOrDefault("Other", 0.0)).cast(DataTypes.DoubleType));
        }

        dfWithOrderedFeatures = dfWithOrderedFeatures.withColumn("birth_order_no_siblings_interaction", col("birth_order").multiply(col("no_siblings").cast(DataTypes.IntegerType)));

        return dfWithOrderedFeatures;
    }
}
