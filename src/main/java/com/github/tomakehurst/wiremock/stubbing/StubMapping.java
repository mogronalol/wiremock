/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.stubbing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.MoreObjects.firstNonNull;

@JsonPropertyOrder({ "id", "uuid", "request", "newRequest", "response" })
public class StubMapping {
	
	public static final int DEFAULT_PRIORITY = 5; 

	private UUID uuid = UUID.randomUUID();

	private boolean persistent;

	private RequestPattern request;

	private ResponseDefinition response;
	private Integer priority;
	private String scenarioName;
	private String requiredScenarioState;
	private String newScenarioState;
	private Scenario scenario;

    private Map<String, Parameters> postServeActions;

	private long insertionIndex;

    private boolean isDirty = true;
	public StubMapping(RequestPattern requestPattern, ResponseDefinition response) {
		setRequest(requestPattern);
		this.response = response;
	}

	public StubMapping() {
		//Concession to Jackson
	}

	public static final StubMapping NOT_CONFIGURED =
	    new StubMapping(null, ResponseDefinition.notConfigured());

    public static StubMapping buildFrom(String mappingSpecJson) {
        return Json.read(mappingSpecJson, StubMapping.class);
    }

    public static String buildJsonStringFor(StubMapping mapping) {
		return Json.write(mapping);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setId(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getId() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

    public boolean shouldBePersisted() {
        return persistent;
    }

    public Boolean isPersistent() {
        return persistent ? true : null;
    }

    public void setPersistent(Boolean persistent) {
        this.persistent = persistent != null && persistent;
    }

    public RequestPattern getRequest() {
		return firstNonNull(request, RequestPattern.ANYTHING);
	}

	public ResponseDefinition getResponse() {
		return firstNonNull(response, ResponseDefinition.ok());
	}

	public void setRequest(RequestPattern request) {
		this.request = request;
	}

	public void setResponse(ResponseDefinition response) {
		this.response = response;
	}

    @Override
	public String toString() {
		return Json.write(this);
	}

	@JsonIgnore
	public long getInsertionIndex() {
		return insertionIndex;
	}

	@JsonIgnore
	public void setInsertionIndex(long insertionIndex) {
		this.insertionIndex = insertionIndex;
	}

    @JsonIgnore
    public boolean isDirty() {
        return isDirty;
    }

    @JsonIgnore
    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getRequiredScenarioState() {
		return requiredScenarioState;
	}

	public void setRequiredScenarioState(String requiredScenarioState) {
		this.requiredScenarioState = requiredScenarioState;
	}

	public String getNewScenarioState() {
		return newScenarioState;
	}

	public void setNewScenarioState(String newScenarioState) {
		this.newScenarioState = newScenarioState;
	}

	public void updateScenarioStateIfRequired() {
		if (isInScenario() && modifiesScenarioState()) {
			scenario.setState(newScenarioState);
		}
	}

	@JsonIgnore
	public Scenario getScenario() {
		return scenario;
	}

	@JsonIgnore
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	@JsonIgnore
	public boolean isInScenario() {
		return scenarioName != null;
	}

	@JsonIgnore
	public boolean modifiesScenarioState() {
		return newScenarioState != null;
	}

	@JsonIgnore
	public boolean isIndependentOfScenarioState() {
		return !isInScenario() || requiredScenarioState == null;
	}

	@JsonIgnore
	public boolean requiresCurrentScenarioState() {
		return !isIndependentOfScenarioState() && requiredScenarioState.equals(scenario.getState());
	}

	public int comparePriorityWith(StubMapping otherMapping) {
		int thisPriority = priority != null ? priority : DEFAULT_PRIORITY;
		int otherPriority = otherMapping.priority != null ? otherMapping.priority : DEFAULT_PRIORITY;
		return thisPriority - otherPriority;
	}

    public Map<String, Parameters> getPostServeActions() {
        return postServeActions;
    }

    public void setPostServeActions(Map<String, Parameters> postServeActions) {
        this.postServeActions = postServeActions;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StubMapping that = (StubMapping) o;
		return isDirty == that.isDirty &&
			Objects.equals(uuid, that.uuid) &&
			Objects.equals(request, that.request) &&
			Objects.equals(response, that.response) &&
			Objects.equals(priority, that.priority) &&
			Objects.equals(scenarioName, that.scenarioName) &&
			Objects.equals(requiredScenarioState, that.requiredScenarioState) &&
			Objects.equals(newScenarioState, that.newScenarioState) &&
			Objects.equals(scenario, that.scenario) &&
			Objects.equals(postServeActions, that.postServeActions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, request, response, priority, scenarioName, requiredScenarioState, newScenarioState, scenario, postServeActions, isDirty);
	}
}
