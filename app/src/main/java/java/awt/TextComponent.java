package java.awt;

public class TextComponent extends Component
{
	public String text;
	public TextComponent() {
		this("");
	}
	
	public TextComponent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
