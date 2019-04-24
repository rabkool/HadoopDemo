package ItemCFstep2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ItemCFReducer2 extends Reducer<Text, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue = new Text();

	// 重写reduce key 结果对应的行号 value 结果对应每一行所对应的列号 加下划线 所对应的值
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		StringBuilder sb = new StringBuilder();
		// 对values进行循环
		for (Text value : values) {
			// 逗号进行分割
			sb.append(value + ",");
		}

		// 行末多余逗号进行删除
		String result = null;
		// 如果以逗号结尾
		if (sb.toString().endsWith(",")) {
			// 长度减一去除逗号
			result = sb.substring(0, sb.length() - 1);

		}

		// 输出的key 行
		outKey.set(key);
		// 输出的value 列 + 值
		outValue.set(result);
		// 进行输出
		context.write(outKey, outValue);

	}

}
