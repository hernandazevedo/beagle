//
/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import BeagleSchema
import XCTest
import SnapshotTesting

final class PathTests: XCTestCase {
    
    func testValidRawRepresentable() {
        // Given
        [
            "client",
            "client2.name",
            "client_[2].matrix[1][1]",
            "[2]",
            "[2][2]"
        ]
        
        // When
        .map {
            Path(rawValue: $0)
        }
        
        // Then
        .forEach {
            XCTAssertNotNil($0)
        }
    }
    
    func testInvalidRawRepresentable() {
        // Given
        [
            "",
            "2client.phones[0]",
            "client.[2]",
            "client[2].[2]",
            "client[a]"
        ]
        
        // When
        .map {
            Path(rawValue: $0)
        }
        
        // Then
        .forEach {
            XCTAssertNil($0)
        }
    }
    
}
