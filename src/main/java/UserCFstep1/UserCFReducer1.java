package UserCFstep1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class UserCFReducer1 extends Reducer<Text, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue = new Text();
	
	// key是物品id     value: id,分值
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		// key是物品id
		String itemID = key.toString();

		// 行为进行统计 建立map容器
		// key:用户id value: 分值 <userID, score>
		Map<String, Integer> map = new HashMap<String, Integer>();

		// 进行分割的到分值
		for (Text value : values) {
			// "_"分割
			String userID = value.toString().split("_")[0];
			String score = value.toString().split("_")[1];

			// 累加操作
			if (map.get(userID) == null) {
				// 容器内新增记录
				map.put(userID, Integer.valueOf(score));
			} else {
				// 获得之前分值
				Integer preScore = map.get(userID);
				// 累加操作
				map.put(userID, preScore + Integer.valueOf(score));

			}
		}

		// 用户id和分值 组成一行 进行输出
		StringBuilder sBuilder = new StringBuilder();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			// 获得用户id
			String userID = entry.getKey();
			// 获得分值
			String score = String.valueOf(entry.getValue());
			// 拼接成矩阵一行
			sBuilder.append(userID + "_" + score + ",");
		}
		
		// 行末多余逗号进行删除
		String line = null;
		// 如果以逗号结尾
		if (sBuilder.toString().endsWith(",")) {
			// 长度减一
			line = sBuilder.substring(0, sBuilder.length() - 1);

		}
		// 设置输出的key 矩阵的行号 商品id
		outKey.set(itemID);
		// 设置输出的value 用户id和分值 组成一行
		outValue.set(line);

		// 进行输出
		context.write(outKey, outValue);

	}

}
