package org.openforis.idm.model;

/**
 * @author G. Miceli
 * @author M. Togna
 */
public class File {

	private String filename;
	private Long size;

	public File(String filename, Long size) {
		this.filename = filename;
		this.size = size;
	}

	public String getFilename() {
		return filename;
	}

	public Long getSize() {
		return size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{filename:").append(filename);
		sb.append(", size:").append(size);
		sb.append("}");
		return sb.toString();
	}
}