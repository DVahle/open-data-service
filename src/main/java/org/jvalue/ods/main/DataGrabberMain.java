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
package org.jvalue.ods.main;

import java.util.LinkedList;
import java.util.List;

import org.ektorp.UpdateConflictException;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.DesignDocument.View;
import org.ektorp.support.DesignDocumentFactory;
import org.ektorp.support.StdDesignDocumentFactory;
import org.jvalue.ods.data.DataSource;
import org.jvalue.ods.data.OdsView;
import org.jvalue.ods.data.sources.OsmSource;
import org.jvalue.ods.data.sources.PegelOnlineSource;
import org.jvalue.ods.data.sources.PegelPortalMvSource;
import org.jvalue.ods.db.DbAccessor;
import org.jvalue.ods.db.DbFactory;
import org.jvalue.ods.db.DbInsertionFilter;
import org.jvalue.ods.db.exception.DbException;
import org.jvalue.ods.grabber.GrabberFilter;
import org.jvalue.ods.logger.Logging;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class DataGrabberMain.
 */
public class DataGrabberMain {

	/** The accessor. */
	private static DbAccessor<JsonNode> accessor;

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Logging.adminLog("Update started");

		accessor = DbFactory.createDbAccessor("ods");
		accessor.connect();
		accessor.deleteDatabase();

		GrabberFilter grabber = new GrabberFilter();
		DbInsertionFilter dbInserter = new DbInsertionFilter(accessor);
		grabber.addFilter(dbInserter);

		List<DataSource> sources = new LinkedList<DataSource>();
		sources.add(PegelOnlineSource.createInstance());
		sources.add(OsmSource.createInstance());
		sources.add(PegelPortalMvSource.createInstance());

		for (DataSource source : sources) {
			Logging.adminLog("grabbing " + source.getId() + " ...");
			grabber.filter(source, null);
		}
		Logging.adminLog("Update completed");

		createCommonViews();

	}


	private static void createCommonViews() {
		createView(new OdsView("_design/ods", "getClassObjectByType",
				"function(doc) { if(doc.objectType) emit (doc.objectType, doc) }"));
		createView(new OdsView("_design/ods", "getAllClassObjects",
				"function(doc) { if(doc.objectType) emit (null, doc) }"));
	}


	private static void createView(OdsView view) {

		DesignDocument dd = null;
		boolean update = true;

		try {
			dd = accessor.getDocument(DesignDocument.class, view.getIdPath());
		} catch (DbException e) {
			DesignDocumentFactory fac = new StdDesignDocumentFactory();
			dd = fac.newDesignDocumentInstance();
			dd.setId(view.getIdPath());
			update = false;
		}

		View v = new DesignDocument.View();
		v.setMap(view.getFunction());
		dd.addView(view.getViewName(), v);

		try {
			if (update) {
				accessor.update(dd);
			} else {
				accessor.insert(dd);
			}
		} catch (UpdateConflictException ex) {
			System.err.println("Design Document already exists."
					+ ex.getMessage());
			Logging.error(DataGrabberMain.class,
					"Design Document already exists." + ex.getMessage());
		}

	}
}
