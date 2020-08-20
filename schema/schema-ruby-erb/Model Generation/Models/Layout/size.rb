#   Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA

#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
 
#       http://www.apache.org/licenses/LICENSE-2.0
 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

require_relative '../../Synthax/Attributes/variable.rb'
require_relative '../base_component.rb'
require_relative '../../Synthax/Types/built_in_type.rb'

class Size < BaseComponent

    def initialize
        unitValue = UnitValue.new.name
        textVariables = [
            Variable.new(:name => "height", :typeName => unitValue, :isOptional => true),
            Variable.new(:name => "maxWidth", :typeName => unitValue, :isOptional => true),
            Variable.new(:name => "maxHeight", :typeName => unitValue, :isOptional => true),
            Variable.new(:name => "minWidth", :typeName => unitValue, :isOptional => true),
            Variable.new(:name => "minHeight", :typeName => unitValue, :isOptional => true),
            Variable.new(:name => "aspectRatio", :typeName => "Double", :isOptional => true)
        ]
        synthaxType = BuiltInType.new(
            :name => self.name,
            :variables => textVariables,
            :package => "br.com.zup.beagle.widget.core"
        )

        super(synthaxType)

    end

end