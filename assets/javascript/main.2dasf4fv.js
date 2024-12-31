function shuffle(array) {
    let currentIndex = array.length, temporaryValue, randomIndex;
    while (0 !== currentIndex) {
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;
        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
    }
    return array;
}

function showRandomAnnouncement(groupId, timeInterval) {
    const announceFastAPI = document.getElementById(groupId);
    if (announceFastAPI) {
        let children = [].slice.call(announceFastAPI.children);
        children = shuffle(children)
        let index = 0
        const announceRandom = () => {
            children.forEach((el, i) => {el.style.display = "none"});
            children[index].style.display = "block"
            index = (index + 1) % children.length
        }
        announceRandom()
        setInterval(announceRandom, timeInterval)
    }
}

async function main() {
    showRandomAnnouncement('announce-left', 5000)
    showRandomAnnouncement('announce-right', 10000)
}

main()
