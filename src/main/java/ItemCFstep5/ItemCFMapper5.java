package ItemCFstep5;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class ItemCFMapper5 extends Mapper<LongWritable, Text, Text, Text> {

	// 输出的key
	private Text outKey = new Text();
	// 输出的value
	private Text outValue = new Text();

	private List<String> cacheList = new ArrayList<String>();

	/**
	 * key:1,2 value:A,1,1 C,3,4
	 */

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

			FileSystem fs = FileSystem.get(URI.create(ItemCFMR5.hdfs + ItemCFMR5.cache), context.getConfiguration());

			FSDataInputStream in = fs.open(new Path(ItemCFMR5.hdfs + ItemCFMR5.cache));
			// FSDataInputStream in = fs.open

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

	// 重写map
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// 获得物品id tab分割
		String item_matrix1 = value.toString().split("\t")[0];
		// 第一个矩阵 用户_评分 逗号分割 用户_评分
		String[] user_score_array_matrix1 = value.toString().split("\t")[1].split(",");

		// 对缓存矩阵遍历
		for (String line : cacheList) {
			// 获取值
			String item_matrix2 = line.toString().split("\t")[0];
			String[] user_sore_array_matrix2 = line.toString().split("\t")[1].split(",");

			// 行首如果id相同进行以下操作
			if (item_matrix1.equals(item_matrix2)) {
				// 遍历矩阵1的列
				for (String user_score_matrix1 : user_score_array_matrix1) {
					boolean flag = false;
					// 进行拆分
					// 下划线分割 用户第0个元素
					String user_matrix1 = user_score_matrix1.split("_")[0];
					// 下划线分割 评分第1个元素
					String score_matrix1 = user_score_matrix1.split("_")[1];

					// 遍历矩阵2的列
					for (String user_score_matrix2 : user_sore_array_matrix2) {
						// 获得矩阵2的用户id
						String user_matrix2 = user_score_matrix2.split("_")[0];
						// 如果用户id相同
						if (user_matrix1.equals(user_matrix2)) {
							flag = true;
						}
					}
					// score_matrix1未对item_matrix1产生行为 输出到最终的列表
				
					if (flag == false) {
						// 输出的key user_matrix1用户的id
						outKey.set(user_matrix1);
						// 输出的value 物品id_评分
						outValue.set(item_matrix1 + "_" + score_matrix1);
						// 输出
						context.write(outKey, outValue);

					}

				}
			}
		}

	}

}
