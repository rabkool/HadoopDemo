package UserCFstep4;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import java.io.IOException;

public class UserCFReducer4 extends Reducer<Text, Text, Text, Text> {
	private Text outKey = new Text();
	private Text outValue = new Text();
	// 重写reduce key 结果对应的行号 value 结果对应每一行所对应的列号 加下划线 所对应的值
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		//value逗号链接
		StringBuilder sb = new StringBuilder();
		for (Text text : values)
			// 逗号进行分割
			sb.append(text + ",");
		
		// 行末多余逗号进行删除
		String res = null;
		// 如果逗号结尾
		if (sb.toString().endsWith(",")) {
			// 长度减一去除逗号
			res = sb.substring(0, sb.length() - 1);
		}
		
		// 输出的key 行
		outKey.set(key);
		// 输出的value 列 + 值
		outValue.set(res);
		// 进行输出
		context.write(outKey, outValue);

	}

}
