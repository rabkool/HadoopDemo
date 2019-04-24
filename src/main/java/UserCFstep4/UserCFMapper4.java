package UserCFstep4;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserCFMapper4 extends Mapper<LongWritable, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue = new Text();

	private List<String> cacheList = new ArrayList<String>();

	// 保留两位小数
	private DecimalFormat df = new DecimalFormat("0.00");

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);

		try {
			// 通过输入流将全局缓存中的 右侧j矩阵读入List<String>中
			// FileReader fr = new FileReader(MR2.hdfs+MR2.cache);
			// BufferedReader br = new BufferedReader(fr);
			// 每行的格式是 行tab 列-值,列值,列-值,列值
			// String line = null;
			// while ((line = br.readLine()) != null) {
			//
			// cacheList.add(line);
			//
			// }
			// fr.close();
			// br.close();

			FileSystem fs = FileSystem.get(URI.create(UserCFMR4.hdfs + UserCFMR4.cache), context.getConfiguration());
			FSDataInputStream in = fs.open(new Path(UserCFMR4.hdfs + UserCFMR4.cache));

			// 每行的格式是 行tab 列-值,列值,列-值,列值
			String line = null;
			while ((line = in.readLine()) != null) {
				cacheList.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	@Override
	/**
	 * 左侧矩阵 key : 行 value : 行 tab 列_值,列_值,列_值,列_值
	 */
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// 左侧
		// 左侧矩阵的第一行 value按tab进行分割
		String row_matrix1 = value.toString().split("\t")[0];
		// 列_值(数组) 第二个元素用逗号进行分割
		String[] column_value_array_matrix1 = value.toString().split("\t")[1].split(",");

		// 对全局缓存进行循环
		for (String line : cacheList) {
			// 右侧
			// line右侧矩阵的每一行
			// 格式 行 tab 列_值,列_值,列_值,列_值
			String row_matrix2 = line.toString().split("\t")[0];
			// 右侧数组 逗号分割
			String[] column_value_array_matrix2 = line.toString().split("\t")[1].split(",");

			// 矩阵两行相乘
			double result = 0;
			// 遍历左矩阵第一行的每一列
			for (String column_value_matrix1 : column_value_array_matrix1) {
				// 获得列号 下划线分割
				String column_matrix1 = column_value_matrix1.split("_")[0];
				// 列号所对应的值下划线分割
				String value_matrix1 = column_value_matrix1.split("_")[1];

				// 遍历右侧矩阵第一行的每一列
				for (String column_value_matrix2 : column_value_array_matrix2) {
					// 如果右侧矩阵的列和左侧矩阵的列是相等的
					if (column_value_matrix2.startsWith(column_matrix1 + "_")) {
						// 取出这一列所对应的值 下划线进行分割
						String value_matrix2 = column_value_matrix2.split("_")[1];
						// 将两列的值相乘并累加
						result += Double.valueOf(value_matrix1) * Double.valueOf(value_matrix2);
					}
				}
			}
			// result是结果矩阵中的某元素 坐标为 行:row_matrix1 , 列 : row_matrix2(右侧矩阵已转换)

			// 等于0跳出循环
			if (result == 0) {
				continue;
			}
			// 输出的key值 第一个矩阵的行
			outKey.set(row_matrix1);
			// 输出的value值 第二个矩阵的行 加下划线 所对应的值 保留2未小数
			outValue.set(row_matrix2 + "_" + df.format(result));
			// 输出 key : 行 value : 列_值
			context.write(outKey, outValue);
		}
	}

}
