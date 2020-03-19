package java.awt.datatransfer;

import java.io.IOException;

public class StringSelection implements Transferable
{

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		// TODO: Implement this method
		return null;
	}
	
	private String data;
	public StringSelection(String data)
	{
		this.data = data;
	}
	public String getString()
	{
		return data;
	}
}
