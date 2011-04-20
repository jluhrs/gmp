package edu.gemini.aspen.gds.keywordssets.factory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.Dataset
import edu.gemini.epics.EpicsReader
import org.scalatest.mock.MockitoSugar
import xml.XML
import java.io.File

@RunWith(classOf[JUnitRunner])
class ObservationStartActorsFactorySpec extends Spec with ShouldMatchers with MockitoSugar {

    describe("A ObservationStartActorsFactory") {
        it("should return a list of Actors") {
            val epicsReader = mock[EpicsReader]
            val file = storeConfiguration

            val startObservationFactory = new ObservationStartActorsFactory(epicsReader, file.getAbsolutePath)

            file.delete
            val dataSet = new Dataset("GS-2011")

            val actors = startObservationFactory.startObservationActors(dataSet)
//            actors should not be('empty)
        }
    }

    def storeConfiguration: File = {
        val xmlConfiguration =
            <factory>
                <!-- Associate channel ws:wsFilter.VALP to keyword KEY1 -->
                <actor keywords="KEY1" class="edu.gemini.aspen.gds.keywords.actors.EpicsValuesActor">
                        <param index="0" service="edu.gemini.epics.EpicsReader"/>
                        <param index="1" value="ws:wsFilter.VALP, ws:wsFilter.VALO"/>
                </actor>
                <!-- Associate channels ws:wsFilter.VALP, ws:wsFilter.VALO to keywords KEY2, KEY3 -->
                <actor keywords="KEY2, KEY3" class="edu.gemini.aspen.gds.keywords.actors.EpicsValuesActor">
                        <param index="0" service="edu.gemini.epics.EpicsReader"/>
                        <param index="1" list="ws:wsFilter.VALP, ws:wsFilter.VALO"/>
                </actor>
                <!-- Associate second array component of tc1:sad:astCtx to keyword KEY4 -->
                <!--actor keywords="KEY4" class="edu.gemini.gds.aspen.keywords.actors.EpicsArrayValuesActor">
                        <param index="0" service="edu.gemini.epics.EpicsReader"/>
                        <param index="1" list="tc1:sad:astCtx"/>
                        <param index="2" value="2"/>
                </actor-->
                <!-- Associate array component 3 and 4 of tc1:sad:astCtx to keywords KEY5, KEY6 -->
                <!--actor keywords="KEY5, KEY6" class="edu.gemini.aspen.gds.keywords.actors.EpicsArrayValuesActor">
                        <param index="0" service="edu.gemini.epics.EpicsReader"/>
                        <param index="1" list="tc1:sad:astCtx"/>
                        <param index="2" list="3, 4"/>
                </actor-->
                <!-- Associate status database with ndame status 1 to keyword KEY7 -->
                <!--actor keywords="KEY7" class="edu.gemini.gds.aspen.keywords.actors.StatusValuesActor">
                        <param index="0" service="edu.gemini.aspen.giapi.status.StatusServiceDatabase"/>
                        <param index="1" value="status1"/>
                </actor-->
            </factory>

        val file = File.createTempFile("conf", "xml")

        XML.save(file.getAbsolutePath, xmlConfiguration, "UTF-8", true, null)

        file
    }
}