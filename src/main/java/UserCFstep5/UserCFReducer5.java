package UserCFstep5;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class UserCFReducer5 extends Reducer<Text, Text, Text, Text> {
	private Text outKey = new Text();
	private Text outValue = new Text();
	
	//key 用户id   value 物品id _ 分值
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		//value逗号链接
		StringBuilder stringBuilder = new StringBuilder();
		for (Text value : values) {
			//逗号进行链接
			stringBuilder.append(value + ",");
		}

		// 行末多余逗号进行删除
		String line = null;
		// 如果逗号结尾
		if (stringBuilder.toString().endsWith(",")) {
			// 长度减一 去除逗号
			line = stringBuilder.substring(0, stringBuilder.length() - 1);
		}
		//输出的key
		outKey.set(key);
		//输出的value
		outValue.set(line);
		//输出
		context.write(outKey, outValue);
	}
}