class TraiCay:
    def __init__(self):
        pass

    def nhap(self, color, vi):
        self.mau_sac = color
        self.vi = vi

    def xuattraicay(self):
        return (self.mau_sac, self.vi)


class TraiBuoi(TraiCay):
    def __init__(self):
        super().__init__()


khoitao = TraiBuoi()
khoitao.nhap("mau xanh", "chua")
print(khoitao.xuattraicay())
