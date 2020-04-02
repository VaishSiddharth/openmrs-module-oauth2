/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.oauth2.web.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MetadataControllerTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private MetadataController controller;

	@Test
	public void shouldReturnOkStatus() {
		assertTrue(controller.getMetadata().getStatusCode() == HttpStatus.OK);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGetMetadata() {
		ResponseEntity<HashMap<String, Object>> res = controller.getMetadata();
		HashMap<String, Object> body = res.getBody();

		assertTrue(body.containsKey("resourceType"));
		assertTrue(body.get("rest") instanceof List);

		List<HashMap<String, Object>> restList = (List<HashMap<String, Object>>) body.get("rest");
		assertTrue(restList.size() == 1);
	}
}
