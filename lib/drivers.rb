
class Drivers

    @@drivers = []

    def self.add(driver)
        @@drivers.push(driver)
        puts @@drivers
    end

    def self.get()
        return @@drivers
    end

end
