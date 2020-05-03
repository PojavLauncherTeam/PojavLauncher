//
//  Copyright (C) 2003 Constantin Kaplinsky.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

//
// CapabilityInfo.java - A class to hold information about a
// particular capability as used in the RFB protocol 3.130.
//
package android.androidVNC;

class CapabilityInfo {

  // Public methods

  public CapabilityInfo(int code,
			String vendorSignature,
			String nameSignature,
			String description) {
    this.code = code;
    this.vendorSignature = vendorSignature;
    this.nameSignature = nameSignature;
    this.description = description;
    enabled = false;
  }

  public CapabilityInfo(int code,
			byte[] vendorSignature,
			byte[] nameSignature) {
    this.code = code;
    this.vendorSignature = new String(vendorSignature);
    this.nameSignature = new String(nameSignature);
    this.description = null;
    enabled = false;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void enable() {
    enabled = true;
  }

  public boolean equals(CapabilityInfo other) {
    return (other != null && this.code == other.code &&
	    this.vendorSignature.equals(other.vendorSignature) &&
	    this.nameSignature.equals(other.nameSignature));
  }

  public boolean enableIfEquals(CapabilityInfo other) {
    if (this.equals(other))
      enable();

    return isEnabled();
  }

  // Protected data

  protected int code;
  protected String vendorSignature;
  protected String nameSignature;

  protected String description;
  protected boolean enabled;
}
