/*
   Copyright 2019, 2020 WeAreFrank!

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package nl.nn.adapterframework.doc.objects;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the Folder object for the IbisDoc application
 *
 * TODO: Rename to FolderJson.
 *
 * @author Chakir el Moussaoui
 */
public class AFolder {

    private @Getter @Setter String name;
    private List<AClass> classes;

    public AFolder(String name) {
        this.name = name;
        this.classes = new ArrayList<>();
    }

    public void addClass(AClass classJson) {
        this.classes.add(classJson);
    }

    public List<AClass> getClasses() {
        return this.classes;
    }
}