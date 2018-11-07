package org.jvalue.ods.transformation;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jvalue.ods.api.views.generic.TransformationFunction;

import javax.script.ScriptException;
import java.io.IOException;

public interface ExecutionEngine {
	ArrayNode execute(ObjectNode data, TransformationFunction transformationFunction, boolean query) throws IOException,
		ScriptException, NoSuchMethodException;
	ArrayNode reduce(ArrayNode resultSet, TransformationFunction transformationFunction) throws IOException,
		ScriptException, NoSuchMethodException;
}
