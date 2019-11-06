package ristogo.ui.menus.forms;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class ChoiceFormField extends FormField
{
	protected LinkedHashMap<Integer, String> values = new LinkedHashMap<>();
	protected int defaultSelection;
	
	public ChoiceFormField(String name, Class<? extends Enum<?>> e)
	{
		this(name, null, e);
	}
	
	public ChoiceFormField(String name, String defaultValue, Class<? extends Enum<?>> e)
	{
		super(name, defaultValue);
		int i = 1;
		String[] values = getEnumNames(e);
		for (String value: values) {
			this.values.put(i, value);
			if (value.equals(defaultValue))
				defaultSelection = i;
			i++;
		}
	}
	
	@Override
	protected boolean isValid()
	{
		return values.containsValue(value);
	}
	
	@Override
	public void setValue(String value)
	{
		if (value == null || value.isBlank())
			value = Integer.toString(defaultSelection);
		this.value = values.get(Integer.parseInt(value));
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(name + ":\n");
		values.forEach((key, value) -> {
			sb.append("\t" + key + ") " + value + "\n");
		});
		sb.append("Enter selection" + (defaultSelection > 0 ? " [" + defaultSelection + "]" : ""));
		return sb.toString();
	}
	
	private static String[] getEnumNames(Class<? extends Enum<?>> e)
	{
		return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
}
