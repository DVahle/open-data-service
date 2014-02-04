/*  Open Data Service
    Copyright (C) 2013  Tsysin Konstantin, Reischl Patrick

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 */
package org.jvalue.ods.db;

import java.util.List;

/**
 * The Interface DbAdapter.
 * 
 */
public interface DbAccessor {

	/**
	 * Gets the document.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param c
	 *            the c
	 * @param id
	 *            the id
	 * @return the document
	 */
	public <T> T getDocument(Class<T> c, String id);

	/**
	 * Gets the last document id.
	 * 
	 * @return the last document id
	 */
	public String getLastDocumentId();

	/**
	 * Insert.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param data
	 *            the data
	 * @return the string
	 */
	public <T> void insert(T data);

	/**
	 * Update.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public void update(Object data);

	/**
	 * Delete database.
	 */
	public void deleteDatabase();

	/**
	 * Connect.
	 */
	public void connect();

	/**
	 * Checks if is connected.
	 * 
	 * @return true, if is connected
	 */
	boolean isConnected();

	/**
	 * Gets the all documents.
	 * 
	 * @return the all documents
	 */
	public List<?> getAllDocuments();

	/**
	 * Execute document query.
	 *
	 * @param designDocId the design doc id
	 * @param viewName the view name
	 * @param key the key
	 * @return the object
	 */
	public Object executeDocumentQuery(String designDocId, String viewName,
			String key);

}
