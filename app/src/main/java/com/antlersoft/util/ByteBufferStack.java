/**
 * 
 */
package com.antlersoft.util;

/**
 * @author Michael A. MacDonald
 *
 */
public class ByteBufferStack {
	private byte[] m_buffer;
	private int[] m_offsets;
	private int m_depth;
	private int m_max_depth;
	private int m_max_size;
	
	public static final int MAX_DEPTH = 20;
	public static final int MAX_SIZE = 1048;
	public ByteBufferStack(int maxDepth, int maxSize)
	{
		m_depth = 0;
		m_max_depth = maxDepth;
		m_max_size = maxSize;
		m_offsets= new int[maxDepth];
		m_buffer = new byte[maxSize];
	}
	
	public ByteBufferStack()
	{
		this(MAX_DEPTH, MAX_SIZE);
	}
	
	/**
	 * 
	 * @return Start of the buffer; this value is valid at least until the next call of reserve()
	 */
	public byte[] getBuffer()
	{
		return m_buffer;
	}
	/**
	 * 
	 * @return Offset in getBuffer() of last reserved region
	 */
	public int getOffset()
	{
		return m_offsets[m_depth];
	}
	
	public int reserve(int count)
	{
		if (count < 0 || (count + m_max_size < 0))
			throw new IllegalArgumentException("Count must by greater than 0");
		if (m_depth == m_max_depth)
		{
			m_max_depth *= 2;
			int[] new_offsets = new int[m_max_depth];
			System.arraycopy(m_offsets, 0, new_offsets, 0, m_depth);
			m_offsets = new_offsets;
		}
		int result = m_offsets[m_depth];
		int new_size = result + count;
		m_offsets[m_depth++] = new_size;
		if (new_size > m_max_size)
		{
			m_max_size = Math.max(2 * m_max_size, new_size);
			byte[] new_buffer = new byte[m_max_size];
			System.arraycopy(m_buffer, 0, new_buffer, 0, result);
			m_buffer = new_buffer;
		}
		
		return result;
	}
	
	public void release()
	{
		if (m_depth<1)
		{
			throw new IllegalStateException("release() without reserve()");
		}
		m_depth--;
	}
}
