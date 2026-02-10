import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        KoinStarterKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
