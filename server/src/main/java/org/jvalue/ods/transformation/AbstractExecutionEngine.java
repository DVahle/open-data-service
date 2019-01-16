/*
 * Copyright (c) 2019 Friedrich-Alexander University Erlangen-Nuernberg (FAU)
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.jvalue.ods.transformation;

import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.script.ScriptException;
import java.io.IOException;

public abstract class AbstractExecutionEngine implements ExecutionEngine {

	protected static final String WRAPPER_FUNCTION = "transformationWrapper";


	@Override
	public abstract ObjectNode execute(ObjectNode data, TransformationFunction transformationFunction)
		throws IOException, ScriptException, NoSuchMethodException;
}
