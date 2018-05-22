/*
 * Licensed to Laurent Broudoux (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Author licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.microcks.util.openapi;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
/**
 * This is a test case for OpenAPISchemaValidtor utility.
 * @author laurent
 */
public class OpenAPISchemaValidatorTest {

   @Test
   public void testValidateJsonSuccess() {
      boolean valid = false;
      String schemaText = null;
      String jsonText = "{\"name\": \"307\", \"model\": \"Peugeot 307\", \"year\": 2003}";

      try {
         // Load schema from file.
         schemaText = FileUtils.readFileToString(
               new File("target/test-classes/io/github/microcks/util/openapi/car-schema.json"));
         // Validate Json according schema.
         valid = OpenAPISchemaValidator.isJsonValid(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }

      // Assert Json object is valid.
      assertTrue(valid);
   }

   @Test
   public void testValidateJsonSuccessWithNull() {
      boolean valid = false;
      String schemaText = null;
      String jsonText = "{\"name\": \"307\", \"model\": null, \"year\": 2003}";

      try {
         // Load schema from file.
         schemaText = FileUtils.readFileToString(
               new File("target/test-classes/io/github/microcks/util/openapi/car-schema.json"));
         // Validate Json according schema.
         valid = OpenAPISchemaValidator.isJsonValid(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }

      // Assert Json object is valid.
      assertTrue(valid);
   }

   @Test
   public void testValidateJsonFailure() {
      boolean valid = true;
      String schemaText = null;
      String jsonText = "{\"id\": \"307\", \"model\": \"Peugeot 307\", \"year\": 2003}";

      try {
         // Load schema from file.
         schemaText = FileUtils.readFileToString(
               new File("target/test-classes/io/github/microcks/util/openapi/car-schema.json"));
         // Validate Json according schema.
         valid = OpenAPISchemaValidator.isJsonValid(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }

      // Assert Json object is not valid.
      assertFalse(valid);

      // Now revalidate and check validation messages.
      List<String> errors = null;
      try {
         errors = OpenAPISchemaValidator.validateJson(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }
      assertEquals(1, errors.size());
      assertEquals("object has missing required properties ([\"name\"])", errors.get(0));
   }

   @Test
   public void testValidateJsonUnknownNodeFailure() {
      boolean valid = true;
      String schemaText = null;
      String jsonText = "{\"name\": \"307\", " +
            "\"model\": \"Peugeot 307\", \"year\": 2003, \"energy\": \"GO\"}";

      try {
         // Load schema from file.
         schemaText = FileUtils.readFileToString(
               new File("target/test-classes/io/github/microcks/util/openapi/car-schema-no-addon.json"));
         // Validate Json according schema.
         valid = OpenAPISchemaValidator.isJsonValid(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }

      // Assert Json object is not valid.
      assertFalse(valid);

      // Now revalidate and check validation messages.
      List<String> errors = null;
      try {
         errors = OpenAPISchemaValidator.validateJson(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }
      assertEquals(1, errors.size());
      assertEquals("object instance has properties which are not allowed by the schema: [\"energy\"]",
            errors.get(0));
   }

   @Test
   public void testValidateJsonSchemaWithReferenceSuccess() {
      boolean valid = true;
      String schemaText = null;
      String jsonText = "[{\"name\": \"307\", \"model\": \"Peugeot 307\", \"year\": 2003}," +
            "{\"name\": \"jean-pierre\", \"model\": \"Peugeot Traveler\", \"year\": 2017}]";

      try {
         // Load schema from file.
         schemaText = FileUtils.readFileToString(
               new File("target/test-classes/io/github/microcks/util/openapi/cars-schema.json"));
         // Validate Json according schema.
         valid = OpenAPISchemaValidator.isJsonValid(schemaText, jsonText);
      } catch (Exception e) {
         e.printStackTrace();
         fail("Exception should not be thrown");
      }

      // Assert Json object is valid.
      assertTrue(valid);
   }

   @Test
   public void testValidateJsonSchemaWithReferenceFailure() {
      boolean valid = true;
      String schemaText = null;
      String jsonText = "[{\"name\": \"307\", \"model\": \"Peugeot 307\", \"year\": 2003}," +
            "{\"name\": \"jean-pierre\", \"model\": \"Peugeot Traveler\", \"year\": \"2017\"}]";

      try {
         // Load schema from file.
         schemaText = FileUtils.readFileToString(
               new File("target/test-classes/io/github/microcks/util/openapi/cars-schema.json"));
         // Validate Json according schema.
         valid = OpenAPISchemaValidator.isJsonValid(schemaText, jsonText);
      } catch (Exception e) {
         e.printStackTrace();
         fail("Exception should not be thrown");
      }

      // Assert Json object is not valid.
      assertFalse(valid);

      List<String> errors = null;
      try {
         errors = OpenAPISchemaValidator.validateJson(schemaText, jsonText);
      } catch (Exception e) {
         fail("Exception should not be thrown");
      }
      // Don't know why but when a failure occurs, validator also complains
      // about components reference not being found.
      assertEquals(2, errors.size());
      assertEquals("the following keywords are unknown and will be ignored: [components]",
            errors.get(0));
      assertEquals("instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])",
            errors.get(1));
   }
}
