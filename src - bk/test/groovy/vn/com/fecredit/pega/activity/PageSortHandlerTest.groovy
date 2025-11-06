package vn.com.fecredit.pega.activity

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import vn.com.fecredit.pega.activity.ActivityBuilder
import vn.com.fecredit.pega.activity.ActivityRunner

class PageSortHandlerTest {

    @Test
    void "page-list ascending sort by numeric property"() {
        def act = ActivityBuilder.activity {
            name 'SortAsc'
            step {
                id 1
                method 'Page-Sort'
                param 'target','clipboard.people'
                param 'by','age'
                param 'order','asc'
            }
        }

        def ctx = [
            clipboard: [
                people: [[name:'alice', age:30], [name:'bob', age:25], [name:'carol', age:28]]
            ]
        ]
        ActivityRunner.run(act, ctx)

        assertEquals('bob', ctx.clipboard.people[0].name)
        assertEquals('carol', ctx.clipboard.people[1].name)
        assertEquals('alice', ctx.clipboard.people[2].name)
    }

    @Test
    void "page-list descending sort by numeric property"() {
        def act = ActivityBuilder.activity {
            name 'SortDesc'
            step {
                id 1
                method 'Page-Sort'
                param 'target','clipboard.people'
                param 'by','age'
                param 'order','desc'
            }
        }

        def ctx = [
            clipboard: [
                people: [[name:'alice', age:30], [name:'bob', age:25], [name:'carol', age:28]]
            ]
        ]
        ActivityRunner.run(act, ctx)

        assertEquals('alice', ctx.clipboard.people[0].name)
        assertEquals('carol', ctx.clipboard.people[1].name)
        assertEquals('bob', ctx.clipboard.people[2].name)
    }
    @Test
    void "page-list bubble sort by numeric property (activity-style)"() {
        def act = ActivityBuilder.activity {
            name 'SortBubble'
            step {
                id 1
                method 'Page-Sort'
                param 'target','clipboard.people'
                param 'by','age'
                param 'order','asc'
                param 'algorithm','bubble'
            }
        }

        def ctx = [
            clipboard: [
                people: [[name:'alice', age:30], [name:'bob', age:25], [name:'carol', age:28], [name:'dave', age:22]]
            ]
        ]
        ActivityRunner.run(act, ctx)

        assertEquals('dave', ctx.clipboard.people[0].name)
        assertEquals('bob', ctx.clipboard.people[1].name)
        assertEquals('carol', ctx.clipboard.people[2].name)
        assertEquals('alice', ctx.clipboard.people[3].name)
    }
}