package org.jvalue.ods.processor.filter;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.jvalue.ods.api.sources.DataSource;
import org.jvalue.ods.api.views.generic.TransformationFunction;
import org.jvalue.ods.data.DataTransformationManager;

public class TransformationFilter extends AbstractFilter<ObjectNode, ObjectNode> {

	private final String transformationFunction;
	private final String reduceFunction;
	private final DataTransformationManager dataTransformationManager;


	@Inject
	public TransformationFilter(
		@Assisted DataSource source,
		MetricRegistry registry,
		DataTransformationManager dataTransformationManager,
		@Assisted String transformationFunction,
		@Assisted String reduceFunction) {
		super(source, registry);
		this.transformationFunction = transformationFunction;
		this.dataTransformationManager = dataTransformationManager;
		this.reduceFunction = reduceFunction;
	}


	@Override
	protected ObjectNode doFilter(ObjectNode node) throws FilterException {
		ArrayNode result;
		try {
			result = dataTransformationManager.transform(node, new TransformationFunction(source.getId(), transformationFunction, reduceFunction), false);
		} catch (Exception e) {
			// nothing to do if exception occurs -> throw it.
			throw new FilterException(e.getMessage(), e);
		}

		if(result.size() != 1){
			throw new FilterException("The transformation function needs to return a valid JSON document.");
		}

		ObjectNode resultObjectNode;
		try{
			resultObjectNode = (ObjectNode) result.get(0);
		}catch(ClassCastException e){
			//object may not be a valid JSON document.
			throw new FilterException("Return value of the transformation function is not a valid JSON document.", e);
		} catch (Exception e){
			throw new FilterException(e.getMessage(), e);
		}

		//check if object has a domainId, if not -> generate a domainId.
		JsonNode at = node.at(source.getDomainIdKey());
		if(at.isMissingNode()){
			resultObjectNode.put(source.getDomainIdKey().getMatchingProperty(), Math.abs(resultObjectNode.hashCode()));
		}
		return resultObjectNode;
	}

	@Override
	protected void doOnComplete() throws FilterException {
	}
}
