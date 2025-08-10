//package com.digiventure.ventnote
//
//import com.digiventure.utils.BaseAcceptanceTest
//import dagger.hilt.android.testing.HiltAndroidTest
//
//@HiltAndroidTest
//class NotesFeature: BaseAcceptanceTest() {
//    @get:Rule(order = 0)
//    var hiltRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    var composeTestRule = createAndroidComposeRule(MainActivity::class.java)
//
//    // Variables related to TopAppBar
//    private lateinit var topAppBar: SemanticsNodeInteraction
//    private lateinit var searchIconButton: SemanticsNodeInteraction
//    private lateinit var menuIconButton: SemanticsNodeInteraction
//    private lateinit var topAppBarTitle: SemanticsNodeInteraction
//    private lateinit var topAppBarTextField: SemanticsNodeInteraction
//    private lateinit var closeSearchIconButton: SemanticsNodeInteraction
//    private lateinit var selectedCount: SemanticsNodeInteraction
//    private lateinit var dropdownSelect: SemanticsNodeInteraction
//    private lateinit var selectAllOption: SemanticsNodeInteraction
//    private lateinit var unselectAllOption: SemanticsNodeInteraction
//    private lateinit var closeSelectIconButton: SemanticsNodeInteraction
//    private lateinit var deleteIconButton: SemanticsNodeInteraction
//    private lateinit var selectedCountContainer: SemanticsNodeInteraction
//
//    private lateinit var navDrawer: SemanticsNodeInteraction
//    private lateinit var rateAppTile: SemanticsNodeInteraction
//
//    private lateinit var noteListRecyclerView: SemanticsNodeInteraction
//    private lateinit var addNoteFloatingActionButton: SemanticsNodeInteraction
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//        Intents.init()
//
//        // Initialize all widgets
//        topAppBar = composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR)
//        searchIconButton = composeTestRule.onNodeWithTag(TestTags.SEARCH_ICON_BUTTON)
//        menuIconButton = composeTestRule.onNodeWithTag(TestTags.MENU_ICON_BUTTON)
//        topAppBarTitle = composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TITLE)
////        topAppBarTextField = composeTestRule.onNodeWithTag(TestTags.TOP_APPBAR_TEXTFIELD)
//        closeSearchIconButton = composeTestRule.onNodeWithTag(TestTags.CLOSE_SEARCH_ICON_BUTTON)
//        selectedCount = composeTestRule.onNodeWithTag(TestTags.SELECTED_COUNT)
//        dropdownSelect = composeTestRule.onNodeWithTag(TestTags.DROPDOWN_SELECT)
//        selectAllOption = composeTestRule.onNodeWithTag(TestTags.SELECT_ALL_OPTION)
//        unselectAllOption = composeTestRule.onNodeWithTag(TestTags.UNSELECT_ALL_OPTION)
//        closeSelectIconButton = composeTestRule.onNodeWithTag(TestTags.CLOSE_SELECT_ICON_BUTTON)
//        deleteIconButton = composeTestRule.onNodeWithTag(TestTags.DELETE_ICON_BUTTON)
//        selectedCountContainer = composeTestRule.onNodeWithTag(TestTags.SELECTED_COUNT_CONTAINER)
//
//        navDrawer = composeTestRule.onNodeWithTag(TestTags.NAV_DRAWER)
//        rateAppTile = composeTestRule.onNodeWithTag(TestTags.RATE_APP_TILE)
//
//        noteListRecyclerView = composeTestRule.onNodeWithTag(TestTags.NOTE_RV)
//        addNoteFloatingActionButton = composeTestRule.onNodeWithTag(TestTags.ADD_NOTE_FAB)
//    }
//
//    @After
//    fun tearDown() {
//        Intents.release()
//    }
//
//    /**
//     * Ensure all top appBar initial functionality
//     * */
//    @Test
//    fun ensureTopAppBarFunctionality() {
//        // Initial state
//        // Scenario : when app is launched, it will show title, menu icon, search icon
//        topAppBar.assertIsDisplayed()
//        searchIconButton.assertIsDisplayed()
//        menuIconButton.assertIsDisplayed()
//        topAppBarTitle.assertIsDisplayed()
//
//        // When search button is pressed
//        // Scenario : when pressed, a text field is shown
//        searchIconButton.performClick()
//        topAppBarTextField.assertIsDisplayed()
//
//        /// 1. When textField is being edited
//        /// Scenario : when pressed, it will gain focus then write text on it
//        topAppBarTextField.performClick()
//        topAppBarTextField.performTextInput("Input Text")
//        composeTestRule.onNodeWithText("Input Text").assertExists()
//
//        /// 2. When close button is pressed
//        /// Scenario : when pressed, textField is dismissed
//        closeSearchIconButton.assertIsDisplayed()
//        closeSearchIconButton.performClick()
//        topAppBarTextField.assertDoesNotExist()
//    }
//
//    /**
//     * Ensure noteList functionality (make sure there are few items)
//     * reside in the local database)
//     * you can use App Inspection -> Databases -> New Query to seed the data) or
//     * simply using add feature.
//     *
//     * Ensure it has three items
//     * (1, "title 1", "note 1", 1678158383000, 1678158383000),
//     * (2, "title 2", "note 2", 1678071983000, 1678071983000),
//     * (3, "title 3", "note 3", 1677899183000, 1677899183000);
//     * */
//    @Test
//    fun ensureNoteListFunctionality() {
//        // Initial state
//        // Scenario : assert if lazy column are displayed (it will not exist if there are no item exists)
//        noteListRecyclerView.assertIsDisplayed()
//
//        /// 1. When the three children is showing
//        /// Scenario : then assert the children by perform scroll and assert displayed
//        val nodeWithText1 = composeTestRule.onNodeWithText("title 1")
//        val nodeWithText2 = composeTestRule.onNodeWithText("title 2")
//        val nodeWithText3 = composeTestRule.onNodeWithText("title 3")
//        nodeWithText1.performScrollTo()
//        nodeWithText2.assertIsDisplayed()
//        nodeWithText3.assertIsDisplayed()
//
//        // When node with text title 1 is long pressed
//        // Scenario : the toolbar will show delete icon, close button, and selected count with
//        // dropdown menu, also the tile checkbox will checked
//        nodeWithText1.performTouchInput {
//            longClick()
//        }
//
//        val checkBoxForNodeWithText1 = composeTestRule.onNodeWithTag("title 1")
//        checkBoxForNodeWithText1.assertIsOn()
//        composeTestRule.onNodeWithText("1").assertIsDisplayed()
//        nodeWithText1.performClick()
//        composeTestRule.onNodeWithText("0").assertIsDisplayed()
//
//        closeSelectIconButton.assertIsDisplayed()
//        deleteIconButton.assertIsDisplayed()
//        selectedCountContainer.assertIsDisplayed()
//
//        /// 1. When selected count container is pressed
//        /// Scenario : it will show dropdown menu with select all and unselect all tile
//        selectedCountContainer.performClick()
//        dropdownSelect.assertIsDisplayed()
//        dropdownSelect.performClick()
//        unselectAllOption.performClick()
//        composeTestRule.onNodeWithText("0").assertIsDisplayed()
//
//        selectedCountContainer.performClick()
//        dropdownSelect.assertIsDisplayed()
//        dropdownSelect.performClick()
//        selectAllOption.performClick()
//        composeTestRule.onNodeWithText("3").assertIsDisplayed()
//
//        /// 2. Delete selected note
//        /// Scenario : it will show loading dialog when delete is being processed
//        /// then it will show snackbar either success or failed
//        /// note : insert the data again after this action
//        deleteIconButton.performClick()
//        // TODO : Assert dialog displayed (it returned error that the dialog show two times at same time)
//        // TODO : the functionality is good when tested manually
//        val dismissButton = composeTestRule.onNodeWithTag(TestTags.DISMISS_BUTTON)
//        dismissButton.assertIsDisplayed()
//        val confirmButton = composeTestRule.onNodeWithTag(TestTags.CONFIRM_BUTTON)
//        confirmButton.assertIsDisplayed()
//
//        dismissButton.performClick()
//        composeTestRule.onNodeWithTag(TestTags.CONFIRMATION_DIALOG).assertDoesNotExist()
//
//        // TODO : check delete action that will show snackBar when success or error
//        // TODO : the functionality is good when tested manually
//
//        /// 2. When close button is pressed
//        /// Scenario : it will turn into initial state
//        closeSelectIconButton.performClick()
//        closeSelectIconButton.assertDoesNotExist()
//
//        // TODO : check filter functionality (it returned error that the note tile show two times at same time)
//        // TODO : the functionality is good when tested manually
//    }
//
//    /**
//     * Ensure all navDrawer initial functionality
//     *
//     * Ensure the emulator / device has play store and an account already
//     * logged in there
//     * */
//    @Test
//    fun ensureNavDrawerFunctionality() {
//        // When menu button is pressed
//        // Scenario : there is hamburger button, when it was pressed a nav drawer will shows
//        menuIconButton.performClick()
//        navDrawer.assertIsDisplayed()
//
//        // When drawer is displayed
//        // Scenario : assert the children is displayed
//        rateAppTile.assertIsDisplayed()
//
//        /// 1. When rate app is pressed
//        /// Scenario : the app will navigated to VentNote PlayStore Page
//        rateAppTile.performClick()
//        intended(hasAction(Intent.ACTION_VIEW))
//        intended(hasData(Uri.parse("https://play.google.com/store/apps/details?id=com.digiventure.ventnote")))
//    }
//}