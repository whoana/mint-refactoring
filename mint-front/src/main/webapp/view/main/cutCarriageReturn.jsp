<%!
	public String cutCarriageReturn(String oriString) {
		int index = oriString.indexOf("\n");
		while(index != -1) {
			String head = oriString.substring(0, index);
			if( index != oriString.length() -1) {
				String tail = oriString.substring(index);
				oriString = head + tail;
			} else {
				oriString = head;
			}
			index = oriString.indexOf("\n");
		}
		return oriString;
	}
%>