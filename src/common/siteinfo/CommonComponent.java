package common.siteinfo;

import java.io.Serializable;
import java.util.Map;

/**
 * 数据抽取组件
 * @author grs
 */
@SuppressWarnings("serial")
public class CommonComponent implements Serializable {
	/** 组件类型，元数据组件，详细数据组件等 */
	protected CollectDataType type;
	/**可扩展元数据组件，存数据库字段*/
	protected Map<String, Component> components;
	
	public CollectDataType getType() {
		return type;
	}
	public void setType(CollectDataType type) {
		this.type = type;
	}
	public Map<String, Component> getComponents() {
		return components;
	}
	public void setComponents(Map<String, Component> components) {
		this.components = components;
	}
	
}
